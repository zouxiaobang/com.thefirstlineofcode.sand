package com.firstlinecode.sand.emulators.lora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LoraNetwork implements ILoraNetwork {
	private static final int DEFAULT_SIGNAL_CRASHED_INTERVAL = 500;
	
	protected Map<LoraAddress, ILoraChip> chips;
	protected List<ILoraNetworkListener> listeners;
	protected Map<LoraChipPair, SignalQuality> signalQualities;
	protected List<LoraSignal> signals;
	protected int signalCrashedInterval;
	
	protected Random arrivedTimeRandomGenerator;
	protected Random signalLostRandomGenerator;
	
	public LoraNetwork() {
		chips = new HashMap<>();
		listeners = new ArrayList<>();
		signalQualities = new HashMap<>();
		signals = new ArrayList<>();
		
		long networkInitTime = System.currentTimeMillis();
		arrivedTimeRandomGenerator = new Random(networkInitTime);
		signalLostRandomGenerator = new Random(networkInitTime);
		signalCrashedInterval = DEFAULT_SIGNAL_CRASHED_INTERVAL;
	}
	
	@Override
	public synchronized ILoraChip createChip(ILoraChip.Type type, byte[] address, int frequencyBand) {
		return createChip(type, new LoraAddress(address, frequencyBand));
	}

	@Override
	public synchronized ILoraChip createChip(ILoraChip.Type type, LoraAddress address) {
		LoraChip chip = new LoraChip(this, type, address);
		if (chips.containsKey(address))
			throw new RuntimeException(String.format("Conflict. Lora chip which's address is %s has ready existed in network.", chip.getAddress()));
		
		chips.put(address, createChip(type, address));
		
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
		int randomSignalLostNumber = signalLostRandomGenerator.nextInt(100);
		
		return randomSignalLostNumber < quality.getPacketLossRate();
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
}
