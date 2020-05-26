package com.firstlinecode.sand.client.lora;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IAddressConfigurator;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.firstlinecode.sand.protocols.lora.dac.Allocated;
import com.firstlinecode.sand.protocols.lora.dac.Allocation;
import com.firstlinecode.sand.protocols.lora.dac.Introduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DynamicAddressConfigurator implements IAddressConfigurator<IDualLoraChipsCommunicator,
			LoraAddress, byte[]>, ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> {
	private static final Logger logger = LoggerFactory.getLogger(DynamicAddressConfigurator.class);
	
	private static final DualLoraAddress ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS = new DualLoraAddress(
			LoraAddress.MAX_TWO_BYTES_ADDRESS, DualLoraAddress.MAX_CHANNEL);

	public enum State {
		WORKING,
		WAITING,
		ALLOCATING,
		ALLOCATED
	}
	
	private IDualLoraChipsCommunicator communicator;
	private IConcentrator concentrator;
	private DualLoraAddress workingAddress;
	private String nodeDeviceId;
	private LoraAddress nodeAddress;
	
	private IObmFactory obmFactory;
	
	private State state;
	
	private List<Listener> listeners;
	
	public DynamicAddressConfigurator(IDualLoraChipsCommunicator communicator, IConcentrator concentrator) {
		this.communicator = communicator;
		this.concentrator =  concentrator;

		obmFactory = ObmFactory.createInstance();
		workingAddress = communicator.getAddress();
		state = State.WORKING;
		
		listeners = new ArrayList<>();
	}
	
	public void start() {
		if (logger.isDebugEnabled()) {
			logger.debug("Starting dynamic address configurator.");
		}
		
		try {
			if (state != State.WORKING || communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS)) {				
				return;
			}
			
			workingAddress = communicator.getAddress();
			communicator.changeAddress(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS);
			
			communicator.addCommunicationListener(this);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Change to address configuration mode. Current address is " + communicator.getAddress());
			}
		} catch (CommunicationException e) {
			throw new RuntimeException("Failed to change address.", e);
		}
		state = State.WAITING;
		
		introduce();
	}
	
	public void stop() {
		if (logger.isDebugEnabled()) {
			logger.debug("Stopping dynamic address configurator.");
		}
		
		if (state == State.WORKING || !communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS)) {				
			logger.warn("It seemed that device has already is being in working mode.");
			return;
		}
		
		communicator.removeCommunicationListener(this);
		
		state = State.WORKING;
		if (communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS)) {
			try {
				communicator.changeAddress(workingAddress);
				
				if (logger.isDebugEnabled()) {
					logger.debug("Change to working mode. Current address is " + communicator.getAddress());
				}
			} catch (CommunicationException e) {
				throw new RuntimeException("Failed to change address.", e);
			}
		}
	}
	
	@Override
	public void introduce() {
		if (state == State.WORKING)
			return;
		
		new Thread(new DataReceiver(), String.format("Data Receiver Thread for %s of %s '%s'",
				this.getClass().getSimpleName(), concentrator.getClass().getSimpleName(),
				concentrator.getDeviceId())).start();
	}
	
	private class DataReceiver implements Runnable {

		@Override
		public void run() {
			while (state != State.WORKING) {
				communicator.receive();
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

	@Override
	public synchronized void negotiate(LoraAddress peerAddress, byte[] data) {
		if (state == State.WORKING) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Receiving address configuration request from %s in working state.", peerAddress));
			}

			return;
		}

		try {
			if (state == State.WAITING) {
				Introduction introduction = (Introduction)obmFactory.toObject(Introduction.class, data);

				nodeDeviceId = introduction.getDeviceId();
				LoraAddress introductedAddress = new LoraAddress(introduction.getAddress(), introduction.getFrequencyBand());

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Receving an intrduction request from %s, %s.", introduction.getAddress(), introduction.getFrequencyBand()));
				}

				Allocation allocation = new Allocation();
				allocation.setGatewayAddress(workingAddress.getSlaveChipAddress().getAddress());
				allocation.setGatewayChannel(workingAddress.getChannel());

				String nodeLanId = concentrator.getBestSuitedNewLanId();
				nodeAddress = new LoraAddress(Long.parseLong(nodeLanId), LoraAddress.DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND);
				allocation.setAllocatedAddress(nodeAddress.getAddress());
				allocation.setAllocatedFrequencyBand(nodeAddress.getFrequencyBand());

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Node allocation: %s: %s => %s.",
							nodeDeviceId, peerAddress, new LoraAddress(allocation.getAllocatedAddress(),
									allocation.getAllocatedFrequencyBand())));
				}

				byte[] response = obmFactory.toBinary(allocation);
				communicator.send(introductedAddress, response);

				state = State.ALLOCATING;
				return;
			}

			if (!nodeAddress.equals(peerAddress)) {
				processParallelAddressConfigurationRequest(peerAddress);
			}

			if (state == State.ALLOCATING) {
				Allocated allocated = (Allocated)obmFactory.toObject(Allocated.class, data);

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Node which's device ID is '%s' has allocated.", allocated.getDeviceId()));
				}
				
				if (!nodeDeviceId.equals(allocated.getDeviceId())) {
					processParallelAddressConfigurationRequest(peerAddress);
				}
				state = State.ALLOCATED;
				
				for (Listener listener : listeners) {
					listener.addressConfigured(nodeDeviceId, nodeAddress);
				}
			}

		} catch (CommunicationException e) {
			// TODO: handle exception
			System.out.println(e);
		} catch (ClassCastException e) {
			// TODO: handle exception
			System.out.println(e);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}


	private void processParallelAddressConfigurationRequest(LoraAddress peerAddress) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Parallel address configuration request from %s.", peerAddress.getAddress()));
		}
		
		throw new ProtocolException(new Conflict(String.format("Parallel address configuration request from %s.", peerAddress.getAddress())));
	}

	@Override
	public void setCommunicator(IDualLoraChipsCommunicator communicator) {
		this.communicator = communicator;
	}
	
	public State getState() {
		return state;
	}
	@Override
	public void sent(LoraAddress to, byte[] data) {
		// NO-OP
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		negotiate(from, data);
	}

	@Override
	public void occurred(CommunicationException e) {
		// NO-OP
	}

	@Override
	public void addressChanged(DualLoraAddress newAddress, DualLoraAddress oldAddress) {
		// NO-OP
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public interface Listener {
		void addressConfigured(String deviceId, LoraAddress address);
	}

}
