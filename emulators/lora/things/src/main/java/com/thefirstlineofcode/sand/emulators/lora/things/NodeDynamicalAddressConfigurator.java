package com.thefirstlineofcode.sand.emulators.lora.things;

import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.IAddressConfigurator;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraCommunicator;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.dac.Allocated;
import com.thefirstlineofcode.sand.protocols.lora.dac.Allocation;
import com.thefirstlineofcode.sand.protocols.lora.dac.Introduction;

public class NodeDynamicalAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, LoraAddress, byte[]>,
		LoraAddress, byte[]>, ICommunicationListener<LoraAddress, LoraAddress, byte[]> {
	private static final int DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL = 1000;
	
	private enum State {
		INITIAL,
		INTRODUCED,
		ALLOCATED
	}
	
	private AbstractLoraThingEmulator thing;
	private LoraCommunicator communicator;
	private IObmFactory obmFactory;
	
	private DualLoraAddress gatewayAddress;
	private LoraAddress allocatedAddress;
	
	private State state;
	
	private DataReceiver dataReceiver = new DataReceiver();
	
	private boolean configurating;
	
	public NodeDynamicalAddressConfigurator(AbstractLoraThingEmulator thing, LoraCommunicator communicator, IObmFactory obmFactory) {
		this.thing = thing;

		this.communicator = communicator;
		communicator.addCommunicationListener(this);
		
		this.obmFactory = obmFactory;

		configurating = false;
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
		
		configurating = true;
		
		dataReceiver = new DataReceiver();
		new Thread(dataReceiver, String.format("Data Receiver Thread for %s of %s '%s'",
				this.getClass().getSimpleName(), thing.getDeviceName(), thing.getDeviceId())).start();
		
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
		
		state = State.INTRODUCED;
	}
	
	public void stop() {
		configurating = false;
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
		if (state != State.INTRODUCED)
			throw new IllegalStateException(String.format("Current state is %s, But it should be State.INTRODUCED.", state));
		
		try {
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
			while (configurating && state != State.ALLOCATED) {
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
		if (configurating) {
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
