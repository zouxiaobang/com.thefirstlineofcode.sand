package com.thefirstlineofcode.sand.client.lora;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.Conflict;
import com.thefirstlineofcode.sand.client.core.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.core.commuication.IAddressConfigurator;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.core.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.client.core.obx.IObxFactory;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.dac.Allocated;
import com.thefirstlineofcode.sand.protocols.lora.dac.Allocation;
import com.thefirstlineofcode.sand.protocols.lora.dac.Introduction;

public class ConcentratorDynamicalAddressConfigurator implements IAddressConfigurator<IDualLoraChipsCommunicator,
			LoraAddress, byte[]>, ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> {
	private static final String NAMESPACE_LORA_DAC = "urn:leps:iot:lora-dac";

	private static final Logger logger = LoggerFactory.getLogger(ConcentratorDynamicalAddressConfigurator.class);
	
	private static final DualLoraAddress ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS = new DualLoraAddress(
			LoraAddress.MAX_TWO_BYTES_ADDRESS, DualLoraAddress.MAX_CHANNEL);

	public enum State {
		STOPPED,
		WAITING,
		ALLOCATING,
		ALLOCATED
	}
	
	private IDualLoraChipsCommunicator communicator;
	private IConcentrator concentrator;
	private DualLoraAddress workingAddress;
	private String nodeDeviceId;
	private LoraAddress nodeAddress;
	
	private IObxFactory obxFactory;
	
	private State state;
	
	private List<Listener> listeners;
	
	public ConcentratorDynamicalAddressConfigurator(IDualLoraChipsCommunicator communicator,
			IConcentrator concentrator, IObxFactory obxFactory) {
		this.communicator = communicator;
		this.concentrator =  concentrator;
		this.obxFactory = obxFactory;
		
		workingAddress = communicator.getAddress();
		state = State.STOPPED;
		
		listeners = new ArrayList<>();
	}
	
	public void start() {
		if (logger.isDebugEnabled()) {
			logger.debug("Starting dynamic address configurator.");
		}
		
		try {
			if (state != State.STOPPED) {
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
		
		if (state == State.STOPPED || !communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS)) {				
			logger.warn("It seemed that address configurator has already is being in stopped mode.");
			return;
		}
		
		communicator.removeCommunicationListener(this);
		
		nodeDeviceId = null;
		nodeAddress = null;
		state = State.STOPPED;
		if (communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS)) {
			try {
				communicator.changeAddress(workingAddress);
				
				if (logger.isDebugEnabled()) {
					logger.debug("Change to working mode. Current address is '{}'.", communicator.getAddress());
				}
			} catch (CommunicationException e) {
				throw new RuntimeException("Failed to change address.", e);
			}
		}
	}
	
	@Override
	public void introduce() {
		if (state == State.STOPPED)
			return;
		
		state = State.WAITING;
		new Thread(new DataReceiver(), String.format("Data Receiver Thread for %s of %s '%s'",
				this.getClass().getSimpleName(), concentrator.getClass().getSimpleName(),
				concentrator.getDeviceName())).start();
	}
	
	private class DataReceiver implements Runnable {

		@Override
		public void run() {
			while (state != State.STOPPED) {
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
		if (state == State.STOPPED) {
			if (logger.isWarnEnabled()) {
				logger.warn("Receiving address configuration request from '{}' in State.STOPPED state.", peerAddress);
			}

			return;
		}
		
		if (nodeAddress != null && !nodeAddress.equals(peerAddress)) {
			processParallelAddressConfigurationRequest(peerAddress);
		}
		
		try {
			if (state == State.WAITING) {
				Introduction introduction = (Introduction)obxFactory.toObject(Introduction.class, data);

				nodeDeviceId = introduction.getDeviceId();
				LoraAddress introductedAddress = new LoraAddress(introduction.getAddress(), introduction.getFrequencyBand());
				
				if (logger.isInfoEnabled()) {
					logger.info("Receving an intrduction request from {}, {}.", introduction.getAddress(), introduction.getFrequencyBand());
				}
				
				Allocation allocation = new Allocation();
				allocation.setGatewayAddress(workingAddress.getSlaveChipAddress().getAddress());
				allocation.setGatewayChannel(workingAddress.getChannel());
				
				String nodeLanId = concentrator.getBestSuitedNewLanId();
				nodeAddress = new LoraAddress(Long.parseLong(nodeLanId), LoraAddress.DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND);
				allocation.setAllocatedAddress(nodeAddress.getAddress());
				allocation.setAllocatedFrequencyBand(nodeAddress.getFrequencyBand());

				if (logger.isInfoEnabled()) {
					logger.info("Node allocation: {}: {} => {}.", nodeDeviceId, peerAddress,
							new LoraAddress(allocation.getAllocatedAddress(),
									allocation.getAllocatedFrequencyBand()));
				};

				byte[] response = obxFactory.toBinary(allocation);
				communicator.send(introductedAddress, response);

				state = State.ALLOCATING;
				return;
			} else if (state == State.ALLOCATING) {
				if (nodeDeviceId == null || nodeAddress == null)
					throw new IllegalStateException("Null node device ID or Null node address.");
				
				Allocated allocated = (Allocated)obxFactory.toObject(Allocated.class, data);

				if (logger.isInfoEnabled()) {
					logger.info("Node which's device ID is '{}' has allocated.", allocated.getDeviceId());
				}
				
				if (!nodeDeviceId.equals(allocated.getDeviceId())) {
					if (logger.isWarnEnabled()) {
						logger.warn("Illegal allocated device. Current device ID of configured device is '{}'." +
								" But device ID of requested device is '{}'.", nodeDeviceId, allocated.getDeviceId());
					}
					
					throw new RuntimeException(String.format("Illegal allocated device. Current device ID of configured device is '%s'." +
							" But device ID of requested device is '%s'.", nodeDeviceId, allocated.getDeviceId()));
				}
				
				state = State.ALLOCATED;
				
				for (Listener listener : listeners) {
					listener.addressConfigured(nodeDeviceId, nodeAddress);
				}
				
				nodeDeviceId = null;
				nodeAddress = null;
				state = State.WAITING;
			} else {
				throw new IllegalStateException(String.format("Illegal configuration state: %s.", state));
			}

		} catch (Exception e) {
			if (logger.isErrorEnabled())
				logger.error("Catch an exception when the concentrator configures the node.", e);
		}
	}


	private void processParallelAddressConfigurationRequest(LoraAddress peerAddress) {
		if (logger.isWarnEnabled()) {
			logger.warn("Parallel address configuration request from '{}'.", peerAddress.getAddress());
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
		Protocol protocol = obxFactory.readProtocol(data);
		if (!NAMESPACE_LORA_DAC.equals(protocol.getNamespace())) {
			return;
		}
		
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
