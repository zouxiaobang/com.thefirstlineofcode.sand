package com.firstlinecode.sand.emulators.lora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LoraNetwork implements ILoraNetwork {
	private static final int DEFAULT_SIGNAL_CRASHED_INTERVAL = 500;
	private static final int DEFAULT_SIGNAL_TRANSFER_TIMEOUT = 2000;
	
	protected Map<LoraAddress, ILoraChip> chips;
	protected List<ILoraNetworkListener> listeners;
	protected Map<LoraChipPair, SignalQuality> signalQualities;
	protected List<LoraSignal> signals;
	
	private Random arrivedTimeRandomGenerator;
	private Random signalLostRandomGenerator;
	
	private volatile int signalCrashedInterval;
	private volatile int signalTransferTimeout;
	
	public LoraNetwork() {
		chips = new HashMap<>();
		listeners = new ArrayList<>();
		signalQualities = new HashMap<>();
		signals = new ArrayList<>();
		
		long networkInitTime = System.currentTimeMillis();
		arrivedTimeRandomGenerator = new Random(networkInitTime);
		signalLostRandomGenerator = new Random(networkInitTime);
		
		signalCrashedInterval = DEFAULT_SIGNAL_CRASHED_INTERVAL;
		signalTransferTimeout = DEFAULT_SIGNAL_TRANSFER_TIMEOUT;
		
		new Thread(new LoraSignalTimeoutThread()).start();
	}
	
	@Override
	public synchronized ILoraChip createChip(ILoraChip.Type type, byte[] address, int frequencyBand) {
		return createChip(type, new LoraAddress(address, frequencyBand));
	}

	@Override
	public synchronized ILoraChip createChip(ILoraChip.Type type, LoraAddress address) {
		if (chips.containsKey(address))
			throw new RuntimeException(String.format("Conflict. Lora chip which's address is %s has ready existed in network.", address));
		
		LoraChip chip = new LoraChip(this, type, address);
		chips.put(address, chip);
		
		return chip;
	}
	
	@Override
	public void setSignalCrashedInterval(int interval) {
		signalCrashedInterval = interval;
	}
	
	@Override
	public int getSignalCrashedInterval() {
		return signalCrashedInterval;
	}
	
	@Override
	public void setSignalTransferTimeout(int timeout) {
		signalTransferTimeout = timeout;
	}
	
	@Override
	public int getSignalTransferTimeout() {
		return signalTransferTimeout;
	}
	
	@Override
	public synchronized void setSingalQuality(ILoraChip chip1, ILoraChip chip2, SignalQuality signalQuality) {
		if (!chips.containsValue(chip1)) {
			throw new IllegalArgumentException(String.format("Can't find lora chip which's address is %s in network.", chip1));
		}
		
		if (!chips.containsValue(chip2)) {
			throw new IllegalArgumentException(String.format("Can't find lora chip which's address is %s in network.", chip1));
		}
		
		signalQualities.put(new LoraChipPair(chip1, chip2), signalQuality);
	}

	@Override
	public synchronized void sendMessage(ILoraChip from, LoraAddress to, byte[] message) {
		try {
			ILoraChip toChip = getChip(to);
			LoraChipPair pair = new LoraChipPair(from, toChip);
			if (!signalQualities.containsKey(pair)) {
				SignalQuality quality = null;
				int randomNumber = new Random().nextInt(10);
				if (randomNumber < 3) {
					quality = SignalQuality.BAD;
				} else if (randomNumber >= 3 && randomNumber < 9) {
					quality = SignalQuality.MEDUIM;
				} else {
					quality = SignalQuality.GOOD;
				}
				
				signalQualities.put(pair, quality);
			}
			
			signals.add(new LoraSignal(from, toChip, message, getArrivedTime(from, toChip, System.currentTimeMillis())));
		} catch (AddressNotFoundException e) {
			for (ILoraNetworkListener listener : listeners) {
				listener.lost(from, to, message);
			}
		}
	}
	
	protected long getArrivedTime(ILoraChip from, ILoraChip to, long sentTime) {
		int randomTime = arrivedTimeRandomGenerator.nextInt(1000);
		
		return sentTime + 1500 - randomTime;
	}
	
	private class AddressNotFoundException extends Exception {
		private static final long serialVersionUID = 8173716761032756998L;		
	}

	protected synchronized ILoraChip getChip(LoraAddress address) throws AddressNotFoundException {
		ILoraChip chip = chips.get(address);
		
		if (chip == null)
			throw new AddressNotFoundException();
		
		return chip;
	}
	
	@Override
	public synchronized LoraMessage receiveMessage(ILoraChip target) {
		LoraSignal received = null;
		for (LoraSignal signal : signals) {
			if (isSendToTarget(signal, target) && isArrived(signal.arrivedTime)) {
				received = signal;
				break;
			}
		}
		
		if (received == null)
			return null;
		
		List<LoraSignal> crashes = findCrashes(received);
		if (!crashes.isEmpty()) {
			crashes.add(received);
			signals.removeAll(crashes);
			
			for (ILoraNetworkListener listener : listeners) {
				for (LoraSignal signal : crashes) {
					listener.crashed(signal.from, signal.to, signal.message);
				}
			}
			
			return null;
		}
		
		signals.remove(received);
		if (isLost(received)) {
			for (ILoraNetworkListener listener : listeners) {
				listener.lost(received.from, received.to.getAddress(), received.message);
			}
		}
		
		return new LoraMessage(received.from.getAddress(), received.message);
	}
	
	private boolean isLost(LoraSignal received) {
		SignalQuality quality = signalQualities.get(new LoraChipPair(received.from, received.to));
		if (received.from.getType() == ILoraChip.Type.HIGH_POWER) {
			quality = adjustHighPowerDeviceSignalQuality(quality);
		}
		
		int randomSignalLostNumber = signalLostRandomGenerator.nextInt(100);
		return randomSignalLostNumber < quality.getPacketLossRate();
	}

	private SignalQuality adjustHighPowerDeviceSignalQuality(SignalQuality quality) {
		if (quality == SignalQuality.MEDUIM || quality == SignalQuality.BAD) {
			quality = SignalQuality.GOOD;
		} else if (quality == SignalQuality.BADDEST) {
			quality = SignalQuality.BAD;
		} else { // quality == SignalQuality.GOOD
			// no-op
		}
		
		return quality;
	}

	private List<LoraSignal> findCrashes(LoraSignal received) {
		List<LoraSignal> crashes = new ArrayList<>();
		for (LoraSignal signal : signals) {
			if (signal == received)
				continue;
			
			if (isCrashed(signal, received)) {
				crashes.add(signal);
			}
		}
		
		return crashes;
	}

	private boolean isCrashed(LoraSignal signal, LoraSignal received) {
		return Math.abs(signal.arrivedTime - received.arrivedTime) < DEFAULT_SIGNAL_CRASHED_INTERVAL;
	}

	private boolean isSendToTarget(LoraSignal signal, ILoraChip target) {
		return signal.to.getAddress().equals(target.getAddress());
	}
	
	private boolean isArrived(long arrivedTime) {
		return System.currentTimeMillis() - arrivedTime > 0;
	}
	
	@Override
	public void addListener(ILoraNetworkListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public boolean removeListener(ILoraNetworkListener listener) {
		return listeners.remove(listener);
	}
	
	private class LoraChipPair {
		public ILoraChip chip1;
		public ILoraChip chip2;
		
		public LoraChipPair(ILoraChip chip1, ILoraChip chip2) {
			if (chip1 == null || chip2 == null)
				throw new IllegalArgumentException("Null lora chip.");
			
			int result = compare(chip1, chip2);
			if (result == 0) {
				throw new RuntimeException("Two lora addresses are same.");
			}
			
			if (result < 0) {
				this.chip1 = chip2;
				this.chip2 = chip1;
			} else {
				this.chip1 = chip1;
				this.chip2 = chip2;
			}
		}

		private int compare(ILoraChip chip1, ILoraChip chip2) {
			return chip1.getAddress().hashCode() - chip2.getAddress().hashCode();
		}
		
		@Override
		public int hashCode() {
			return chip1.hashCode() + chip2.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof LoraChipPair) {
				LoraChipPair other = (LoraChipPair)obj;
				
				return chip1.equals(other.chip1) && chip2.equals(other.chip2);
			}
			
			return false;
		}
	}
	
	private class LoraSignal {
		public ILoraChip from;
		public ILoraChip to;
		public byte[] message;
		public long arrivedTime;
		
		public LoraSignal(ILoraChip from, ILoraChip to, byte[] message, long arrivedTime) {
			this.from = from;
			this.to = to;
			this.message = message;
			this.arrivedTime = arrivedTime;
		}
	}
	
	private class LoraSignalTimeoutThread implements Runnable {

		@Override
		public void run() {
			synchronized (LoraNetwork.this) {
				long currentTime = System.currentTimeMillis();
				List<LoraSignal> timeouts = new ArrayList<>();
				for (LoraSignal signal : signals) {
					if (isTimeout(currentTime, signal)) {
						timeouts.add(signal);
					}
				}
				
				if (!timeouts.isEmpty()) {
					signals.removeAll(timeouts);
					
					for (LoraSignal signal : timeouts) {
						for (ILoraNetworkListener listener : listeners) {
							listener.lost(signal.from, signal.to.getAddress(), signal.message);
						}
					}
				}
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private boolean isTimeout(long currentTime, LoraSignal signal) {
			return currentTime - signal.arrivedTime > signalTransferTimeout;
		}
		
	}

	@Override
	public synchronized void changeAddress(ILoraChip oldChip, LoraAddress newAddress) {
		ILoraChip newChip = createChip(oldChip.getType(), newAddress);
		
		LoraChipPair oldPair = null;
		LoraChipPair newPair = null;
		for (LoraChipPair pair : signalQualities.keySet()) {
			if (pair.chip1.equals(oldChip)) {
				oldPair = pair;
				newPair = new LoraChipPair(newChip, pair.chip2);
				break;
			}
			
			if (pair.chip2.equals(oldChip)) {
				oldPair = pair;
				newPair = new LoraChipPair(pair.chip1, newChip);
				break;
			}
		}
		
		if (oldPair != null) {
			SignalQuality quality = signalQualities.remove(oldPair);
			signalQualities.put(newPair, quality);
		}
		
		chips.remove(oldChip.getAddress());
	}
}
