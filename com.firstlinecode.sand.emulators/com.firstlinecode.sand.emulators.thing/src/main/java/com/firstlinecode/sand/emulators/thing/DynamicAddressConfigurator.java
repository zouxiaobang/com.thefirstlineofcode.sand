package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IAddressConfigurator;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.firstlinecode.sand.protocols.lora.dac.Allocation;
import com.firstlinecode.sand.protocols.lora.dac.Allocated;
import com.firstlinecode.sand.protocols.lora.dac.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, LoraAddress, byte[]>,
		LoraAddress, byte[]>, ICommunicationListener<LoraAddress, LoraAddress, byte[]> {
	private static final int DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL = 1000;
	
	private enum State {
		INITIAL,
		INTRUDUCED,
		ALLOCATED
	}
	
	private IThingEmulator thing;
	private LoraCommunicator communicator;
	private IObmFactory obmFactory;
	
	private DualLoraAddress gatewayAddress;
	private LoraAddress allocatedAddress;
	
	private State state;
	
	private DataReceiver dataReceiver = new DataReceiver();
	
	public DynamicAddressConfigurator(IThingEmulator thing, LoraCommunicator communicator) {
		this.thing = thing;
		this.communicator = communicator;
		obmFactory = new ObmFactory();
		
		communicator.addCommunicationListener(this);
	}

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, LoraAddress, byte[]> communicator) {
		this.communicator = (LoraCommunicator)communicator;
		communicator.addCommunicationListener(this);
	}
	
	@Override
	public void introduce() {
		doIntroduce();
	}
	
	private synchronized void doIntroduce() {
		resetToInitialState();
		
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
				
				Allocated allocated = new Allocated();
				allocated.setDeviceId(thing.getDeviceId());
				
				byte[] response = obmFactory.toBinary(allocated);
				communicator.send(LoraAddress.DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS, response);
				done(gatewayAddress, communicator.getAddress());
				
				state = State.ALLOCATED;
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
		private boolean stop = false;

		@Override
		public void run() {
			stop = false;
			while (!stop && state != State.ALLOCATED) {
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
	
	public void done(DualLoraAddress gatewayAddress, LoraAddress thingAddress) {
		thing.addressConfigured(gatewayAddress.getMasterChipAddress(), gatewayAddress.getSlaveChipAddress(),
				thingAddress);
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		// NO-OP
	}

	@Override
	public synchronized void confirm() {
		// NO-OP
	}
	
}
