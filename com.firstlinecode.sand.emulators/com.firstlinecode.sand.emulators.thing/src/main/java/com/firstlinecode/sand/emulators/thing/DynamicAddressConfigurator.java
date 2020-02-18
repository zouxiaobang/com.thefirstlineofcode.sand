package com.firstlinecode.sand.emulators.thing;

import java.util.Timer;
import java.util.TimerTask;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.commuication.IObmFactory;
import com.firstlinecode.sand.client.things.commuication.ObmFactory;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.protocols.core.lora.Allocation;
import com.firstlinecode.sand.protocols.core.lora.Confirmation;
import com.firstlinecode.sand.protocols.core.lora.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, byte[]>,
		LoraAddress, byte[]>, ICommunicationListener<LoraAddress, byte[]> {
	private static final int DEFAULT_ADDRESS_CONFIGURATION_NEGOTIATION_TIMEOUT = 1000 * 10;
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
	
	private LoraAddress gatewayAddress;
	private LoraAddress allocatedAddress;
	
	private State state;
	
	private Timer timeoutTimer;
	
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
				if (state != State.CONFIRMED && state != State.ALLOCATED) {
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
		communicator.send(LoraAddress.DEFAULLT_ADDRESS_CONFIGURATOR_NEGOTIATION_ADDRESS,
				obmFactory.toBinary(introduction));
	}
	
	public void stop() {
		timeoutTimer.cancel();
		timeoutTimer = null;
		
		resetToInitialState();
	}

	private void resetToInitialState() {
		gatewayAddress = null;
		allocatedAddress = null;
		state = State.INITIAL;
	}
	
	@Override
	public synchronized void negotiate(LoraAddress peerAddress, byte[] data) {
		try {
			if (state == State.INITIAL) {
				Allocation allocation = (Allocation)obmFactory.toObject(Allocation.class, data);
				gatewayAddress = new LoraAddress(allocation.getGatewayAddress(), allocation.getGatewayFrequencyBand());
				allocatedAddress = new LoraAddress(allocation.getAllocatedAddress(), allocation.getAllocatedFrequencyBand());
				
				communicator.changeAddress(allocatedAddress);
				Confirmation confirmation = new Confirmation();
				confirmation.setDeviceId(deviceId);
				
				byte[] response = obmFactory.toBinary(confirmation);
				communicator.send(LoraAddress.DEFAULLT_ADDRESS_CONFIGURATOR_NEGOTIATION_ADDRESS, response);
				
				state = State.ALLOCATED;
			} else if (state == State.ALLOCATED) {
				System.out.println("Allocated");
			} else { // state == State.CONFIRMED
				
			}
		} catch (Exception e) {
			// ignore
		}
	}
	
	private class DataReceiver implements Runnable {

		@Override
		public void run() {
			while (state != State.CONFIRMED) {
				LoraData data = communicator.receive();
				
				if (data != null) {
					negotiate(data.getAddress(), data.getData());
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// ignore
				}
			}
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
	
	public void done(LoraAddress address) {
		
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		// No-Op
	}

	@Override
	public synchronized void confirm() {
		// TODO Auto-generated method stub
		
	}
	
}
