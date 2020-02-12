package com.firstlinecode.sand.emulators.thing;

import java.util.Timer;
import java.util.TimerTask;

import com.firstlinecode.sand.client.lora.LoraAddress;
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
	private static final int DEFAULT_ADDRESS_CONFIGURATION_TIMEOUT = 5000;
	
	private enum State {
		INITIAL,
		INTRUDUCED,
		ALLOCATED,
		CONFIRMED
	}
	
	private String deviceId;
	private ICommunicator<LoraAddress, byte[]> communicator;
	private IObmFactory obmFactory;
	
	private LoraAddress gatewayAddress;
	private LoraAddress allocatedAddress;
	
	private Thread executorThread;
	
	private State state;
	
	public DynamicAddressConfigurator(String deviceId, LoraCommunicator communicator) {
		this.deviceId = deviceId;
		this.communicator = communicator;
		obmFactory = new ObmFactory();
		
		communicator.addCommunicationListener(this);
	}

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, byte[]> communicator) {
		this.communicator = communicator;
		communicator.addCommunicationListener(this);
	}
	
	@Override
	public void introduce() {
		executorThread = new Thread(new ExecutorThread());
		executorThread.start();
		
		runTimeoutTask();
	}
	
	private class ExecutorThread implements Runnable {
		@Override
		public void run() {
			gatewayAddress = null;
			allocatedAddress = null;
			state = State.INITIAL;
			
			Introduction introduction = new Introduction();
			introduction.setDeviceId(deviceId);
			introduction.setAddress(LoraAddress.DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_ADDRESS);
			introduction.setFrequencyBand(LoraAddress.DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_SLAVE_CHIP_FREQUENCE_BAND);
			communicator.send(LoraAddress.DEFAULLT_ADDRESS_CONFIGURATOR_NEGOTIATION_ADDRESS,
					obmFactory.toBinary(introduction));
		}
	}

	private void runTimeoutTask() {
		Timer timeoutTimer = new Timer();
		timeoutTimer.schedule(new TimerTask() {	
			@Override
			public void run() {
				executorThread.interrupt();
				executorThread = new Thread(new ExecutorThread());
				executorThread.start();
				
				runTimeoutTask();
			}
		}, DEFAULT_ADDRESS_CONFIGURATION_TIMEOUT);
	}
	
	@Override
	public void negotiate(LoraAddress peerAddress, byte[] data) {
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
			}
		} catch (Exception e) {
			// ignore
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}
	
}
