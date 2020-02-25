package com.firstlinecode.sand.emulators.thing;

import java.util.Timer;
import java.util.TimerTask;

import com.firstlinecode.sand.client.lora.DualLoraAddress;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.commuication.IObmFactory;
import com.firstlinecode.sand.client.things.commuication.ObmFactory;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.protocols.lora.dac.Allocation;
import com.firstlinecode.sand.protocols.lora.dac.Confirmation;
import com.firstlinecode.sand.protocols.lora.dac.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, byte[]>,
		LoraAddress, byte[]>, ICommunicationListener<LoraAddress, byte[]> {
	private static final int DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL = 1000;
	private static final int DEFAULT_ADDRESS_CONFIGURATION_NEGOTIATION_TIMEOUT = 1000 * 30;
	private static final int DEFAULT_ADDRESS_CONFIGURATION_CONFIRMATION_TIMEOUT = 1000 * 60 * 2;
	
	private enum State {
		INITIAL,
		INTRUDUCED,
		ALLOCATED,
		CONFIRMED
	}
	
	private String deviceId;
	private LoraCommunicator communicator;
	private IObmFactory obmFactory;
	
	private DualLoraAddress gatewayAddress;
	private LoraAddress allocatedAddress;
	
	private State state;
	
	private Timer timeoutTimer;
	private DataReceiver dataReceiver = new DataReceiver();
	
	public DynamicAddressConfigurator(String deviceId, LoraCommunicator communicator) {
		this.deviceId = deviceId;
		this.communicator = communicator;
		obmFactory = new ObmFactory();
		
		communicator.addCommunicationListener(this);
	}

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, byte[]> communicator) {
		this.communicator = (LoraCommunicator)communicator;
		communicator.addCommunicationListener(this);
	}
	
	@Override
	public void introduce() {
		doIntroduce();
		
		timeoutTimer = new Timer();
		timeoutTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (state != State.ALLOCATED && state != State.CONFIRMED) {
					doIntroduce();
				}
			}
			
		}, DEFAULT_ADDRESS_CONFIGURATION_NEGOTIATION_TIMEOUT,
			DEFAULT_ADDRESS_CONFIGURATION_NEGOTIATION_TIMEOUT);
	}
	
	private synchronized void doIntroduce() {
		resetToInitialState();
		
		Introduction introduction = new Introduction();
		introduction.setDeviceId(deviceId);
		introduction.setAddress(communicator.getAddress().getAddress());
		introduction.setFrequencyBand(communicator.getAddress().getFrequencyBand());
		communicator.send(LoraAddress.DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS,
				obmFactory.toBinary(introduction));
	}
	
	public void stop() {
		timeoutTimer.cancel();
		timeoutTimer = null;
		
		resetToInitialState();
	}

	private void resetToInitialState() {
		if (dataReceiver != null) {
			dataReceiver.stop();
		}
		
		dataReceiver = new DataReceiver();
		new Thread(dataReceiver).start();
		
		gatewayAddress = null;
		allocatedAddress = null;
		state = State.INITIAL;
	}
	
	@Override
	public synchronized void negotiate(LoraAddress peerAddress, byte[] data) {
		try {
			if (state == State.INITIAL) {
				Allocation allocation = (Allocation)obmFactory.toObject(Allocation.class, data);
				gatewayAddress = new DualLoraAddress(allocation.getGatewayAddress(), allocation.getGatewayChannel());
				allocatedAddress = new LoraAddress(allocation.getAllocatedAddress(), allocation.getAllocatedFrequencyBand());
				
				communicator.changeAddress(allocatedAddress);
				
				Confirmation confirmation = new Confirmation();
				confirmation.setDeviceId(deviceId);
				
				byte[] response = obmFactory.toBinary(confirmation);
				communicator.send(LoraAddress.DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS, response);
				
				state = State.ALLOCATED;
			} else if (state == State.ALLOCATED) {
				Confirmation confirmation = (Confirmation)obmFactory.toObject(Confirmation.class, data);
				if (!deviceId.equals(confirmation.getDeviceId())) {
					throw new IllegalStateException("Device ID doesn't match.");
				}
				
				done(gatewayAddress, communicator.getAddress());
			} else { // state == State.CONFIRMED
				// It's impossible the code goes to here.
			}
		} catch (Exception e) {
			// ignore
		}
	}
	
	private class DataReceiver implements Runnable {
		private boolean stop = false;

		@Override
		public void run() {
			stop = false;
			while (!stop && state != State.CONFIRMED) {
				LoraData data = communicator.receive();
				
				if (data != null) {
					if (state != State.ALLOCATED) {						
						negotiate(data.getAddress(), data.getData());
					} else {
						confirm();
					}
				}
				
				try {
					Thread.sleep(DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
		
		public void stop() {
			this.stop = true;
		}
		
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {}

	@Override
	public void received(LoraAddress from, byte[] data) {
		negotiate(from, data);
	}

	@Override
	public void occurred(CommunicationException e) {
		// TODO Auto-generated method stub
		
	}
	
	public void done(DualLoraAddress gatewayAddress, LoraAddress address) {
		System.out.println(String.format("Address configuration done. Gateway address is %s. Device address is %s ",
				gatewayAddress, address));		
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		// No-Op
	}

	@Override
	public synchronized void confirm() {
		timeoutTimer.cancel();
		timeoutTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (state != State.CONFIRMED) {
					doIntroduce();
				}
			}
			
		}, DEFAULT_ADDRESS_CONFIGURATION_CONFIRMATION_TIMEOUT, 0);
	}
	
}
