package com.firstlinecode.sand.emulators.thing;

import java.util.ConcurrentModificationException;

import com.firstlinecode.chalk.IOrder;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IAddressConfigurator;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.firstlinecode.sand.protocols.lora.dac.Allocated;
import com.firstlinecode.sand.protocols.lora.dac.Allocation;
import com.firstlinecode.sand.protocols.lora.dac.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, LoraAddress, ObmData>,
		LoraAddress, ObmData> {
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
	
	private ParsingProcessor parsingProcessor = new ParsingProcessor();
	private NegotiationProcessor negotiationProcessor = new NegotiationProcessor();
	
	private DataReceiver dataReceiver = new DataReceiver();
	
	public DynamicAddressConfigurator(IThingEmulator thing, LoraCommunicator communicator) {
		this.thing = thing;
		obmFactory = ObmFactory.createInstance();

		this.communicator = communicator;
		communicator.addCommunicationListener(parsingProcessor);
		communicator.addCommunicationListener(negotiationProcessor);
	}

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, LoraAddress, ObmData> communicator) {
		this.communicator.removeCommunicationListener(parsingProcessor);
		this.communicator.removeCommunicationListener(negotiationProcessor);
		
		this.communicator = (LoraCommunicator)communicator;
		communicator.addCommunicationListener(parsingProcessor);
		communicator.addCommunicationListener(negotiationProcessor);
	}
	
	@Override
	public void introduce() {
		doIntroduce();
	}
	
	private synchronized void doIntroduce() {
		resetToInitialState();
		
		dataReceiver = new DataReceiver();
		new Thread(dataReceiver).start();
		
		Introduction introduction = new Introduction();
		introduction.setDeviceId(thing.getDeviceId());
		introduction.setAddress(communicator.getAddress().getAddress());
		introduction.setFrequencyBand(communicator.getAddress().getFrequencyBand());
		try {
			communicator.send(LoraAddress.DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS,
					new ObmData(introduction, obmFactory.toBinary(introduction)));
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if (dataReceiver != null) {
			dataReceiver.stop();
			dataReceiver = null;
		}
	}

	private void resetToInitialState() {
		stop();
		
		gatewayAddress = null;
		allocatedAddress = null;
		state = State.INITIAL;
	}
	
	public synchronized void parse(LoraAddress peerAddress, ObmData data) {
		if (state == State.INITIAL) {
			Allocation allocation = (Allocation)obmFactory.toObject(Allocation.class, data.getBinary());
			data.setProtocolObject(allocation);
		}
	}
	
	@Override
	public synchronized void negotiate(LoraAddress peerAddress, ObmData data) {
		try {
			if (state == State.INITIAL) {
				Allocation allocation = data.getProtocolObject();
				
				gatewayAddress = new DualLoraAddress(allocation.getGatewayAddress(), allocation.getGatewayChannel());
				allocatedAddress = new LoraAddress(allocation.getAllocatedAddress(), allocation.getAllocatedFrequencyBand());
				
				communicator.changeAddress(allocatedAddress);
				
				Allocated allocated = new Allocated();
				allocated.setDeviceId(thing.getDeviceId());
				
				byte[] response = obmFactory.toBinary(allocated);
				communicator.send(LoraAddress.DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS, new ObmData(allocated, response));
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
		private boolean stop = false;

		@Override
		public void run() {
			stop = false;
			while (!stop && state != State.ALLOCATED) {
				communicator.receive();

				try {
					Thread.sleep(DEFAULT_ADDRESS_CONFIGURATION_DATA_RETRIVE_INTERVAL);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			
			// Remove communication listeners here to avoid ConcurrentModificationException.
			communicator.removeCommunicationListener(parsingProcessor);
			communicator.removeCommunicationListener(negotiationProcessor);
		}
		
		public void stop() {
			this.stop = true;
		}
		
	}

	
	public void done(DualLoraAddress gatewayAddress, LoraAddress thingAddress) {
		thing.addressConfigured(gatewayAddress.getMasterChipAddress(), gatewayAddress.getSlaveChipAddress(),
				thingAddress);
	}


	@Override
	public synchronized void confirm() {
		// NO-OP
	}

	/**
	 * use to parse the binary data, translate to protocol object
	 */
	private class ParsingProcessor implements ICommunicationListener<LoraAddress, LoraAddress, ObmData>, IOrder {
		@Override
		public void sent(LoraAddress to, ObmData data) {
			// NO-OP
		}

		@Override
		public void received(LoraAddress from, ObmData data) {
			parse(from, data);
		}

		@Override
		public void occurred(CommunicationException e) {
			// NO-OP
		}

		@Override
		public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
			// NO-OP
		}

		@Override
		public int getOrder() {
			return ORDER_MAX;
		}
	}

	/**
	 * use to negotiate the binary data, translate to protocol object
	 */
	private class NegotiationProcessor implements ICommunicationListener<LoraAddress, LoraAddress, ObmData>, IOrder {
		@Override
		public void sent(LoraAddress to, ObmData data) {
			// NO-OP
		}

		@Override
		public void received(LoraAddress from, ObmData data) {
			negotiate(from, data);
		}

		@Override
		public void occurred(CommunicationException e) {
			// NO-OP
		}

		@Override
		public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
			// NO-OP
		}

		@Override
		public int getOrder() {
			return ORDER_MIN;
		}
	}
}
