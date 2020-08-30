package com.firstlinecode.sand.emulators.lora.thing;

import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IAddressConfigurator;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.emulators.lora.network.LoraCommunicator;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.firstlinecode.sand.protocols.lora.dac.Allocated;
import com.firstlinecode.sand.protocols.lora.dac.Allocation;
import com.firstlinecode.sand.protocols.lora.dac.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, LoraAddress, byte[]>,
		LoraAddress, byte[]>, ICommunicationListener<LoraAddress, LoraAddress, byte[]> {
	private static final int DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL = 1000;
	
	private enum State {
		INITIAL,
		INTRUDUCED,
		ALLOCATED
	}
	
	private AbstractLoraThingEmulator thing;
	private LoraCommunicator communicator;
	private IObmFactory obmFactory;
	
	private DualLoraAddress gatewayAddress;
	private LoraAddress allocatedAddress;
	
	private State state;
	
	private DataReceiver dataReceiver = new DataReceiver();
	
	private boolean working;
	
	public DynamicAddressConfigurator(AbstractLoraThingEmulator thing, LoraCommunicator communicator) {
		this.thing = thing;
		obmFactory = ObmFactory.createInstance();

		this.communicator = communicator;
		communicator.addCommunicationListener(this);

		working = false;
	}

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, LoraAddress, byte[]> communicator) {
		this.communicator.removeCommunicationListener(this);

		this.communicator = (LoraCommunicator)communicator;
		communicator.addCommunicationListener(this);
	}
	
	@Override
	public void introduce() {
		doIntroduce();
	}
	
	private synchronized void doIntroduce() {
		resetToInitialState();
		
		working = true;
		
		dataReceiver = new DataReceiver();
		new Thread(dataReceiver, String.format("Data Receiver Thread for %s of %s '%s'",
				this.getClass().getSimpleName(), thing.getThingName(), thing.getDeviceId())).start();
		
		Introduction introduction = new Introduction();
		introduction.setDeviceId(thing.getDeviceId());
		introduction.setAddress(communicator.getAddress().getAddress());
		introduction.setFrequencyBand(communicator.getAddress().getFrequencyBand());
		try {
			communicator.send(LoraAddress.DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS,
					obmFactory.toBinary(introduction));
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		working = false;
		dataReceiver = null;
	}

	private void resetToInitialState() {
		stop();
		
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
				
				Allocated allocated = new Allocated();
				allocated.setDeviceId(thing.getDeviceId());
				
				byte[] response = obmFactory.toBinary(allocated);
				communicator.send(LoraAddress.DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS, response);
				state = State.ALLOCATED;
				
				// Waiting for data receiver to stop.
				try {
					Thread.sleep(DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL);
				} catch (InterruptedException e) {
					// Ignore
				}
				
				done(gatewayAddress, communicator.getAddress());				
			} else { // state == State.ALLOCATED
				// Code shouldn't go to here.
				throw new IllegalStateException("Thing address has allocated.");
			}
		} catch (ClassCastException e) {
			// ignore
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class DataReceiver implements Runnable {
		@Override
		public void run() {
			while (working && state != State.ALLOCATED) {
				communicator.receive();

				try {
					Thread.sleep(DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}		
	}

	
	public void done(DualLoraAddress gatewayAddress, LoraAddress thingAddress) {
		thing.addressConfigured(gatewayAddress.getMasterChipAddress(), gatewayAddress.getSlaveChipAddress(),
				thingAddress);
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {
		// NO-OP
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		if (working) {
			negotiate(from, data);
		}
	}

	@Override
	public void occurred(CommunicationException e) {
		// NO-OP
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		// NO-OP
	}
}
