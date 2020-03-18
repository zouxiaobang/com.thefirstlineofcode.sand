package com.firstlinecode.sand.client.lora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.sand.client.concentrator.IConcentrator;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IAddressConfigurator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;
import com.firstlinecode.sand.protocols.core.CommunicationNet;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.firstlinecode.sand.protocols.lora.dac.Allocation;
import com.firstlinecode.sand.protocols.lora.dac.Confirmation;
import com.firstlinecode.sand.protocols.lora.dac.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<IDualLoraChipCommunicator, LoraAddress, byte[]> {
	private static final Logger logger = LoggerFactory.getLogger(DynamicAddressConfigurator.class);
	
	private static final DualLoraAddress ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS = new DualLoraAddress(
			LoraAddress.MAX_TWO_BYTES_ADDRESS, DualLoraAddress.MAX_CHANNEL);
	
	public enum State {
		WORKING,
		WAITING,
		ALLOCATING,
		CONFIRMING
	}
	
	private IDualLoraChipCommunicator communicator;
	private IConcentrator concentrator;
	private DualLoraAddress workingAddress;
	private String nodeDeviceId;
	private LoraAddress nodeAddress;
	
	private IObmFactory obmFactory;
	
	private State state;
	
	public DynamicAddressConfigurator(IDualLoraChipCommunicator communicator, IConcentrator concentrator) {
		this.communicator = communicator;
		this.concentrator =  concentrator;
		
		obmFactory = new ObmFactory();
		workingAddress = communicator.getAddress();
		state = State.WORKING;
	}
	
	public void start() {
		if (logger.isDebugEnabled()) {
			logger.debug("Starting dynamic address configurator.");
		}
		
		try {
			if (state != State.WORKING || communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS)) {				
				throw new IllegalStateException("It seemed that device is being in address configuration mode.");
			}
			
			workingAddress = communicator.getAddress();
			communicator.changeAddress(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS);
			
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
			throw new IllegalStateException("It seemed that device is being in working mode.");
		}
		
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
		
		new Thread(new DataReceiver()).start();
	}
	
	private class DataReceiver implements Runnable {

		@Override
		public void run() {
			while (state != State.WORKING) {
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
				allocation.setGatewayAddress(workingAddress.getSlaveAddress().getAddress());
				allocation.setGatewayChannel(workingAddress.getChannel());
				
				// TODO Use chalk concentrator plugin to get size of nodes.
				/* int nodesSize = concentrator.getLanIds().length;
				int iNodeLanId = nodesSize + 1;
				nodeLanId = Integer.toString(iNodeLanId);
				nodeAddress = new LoraAddress(iNodeLanId, LoraAddress.DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND);
				allocation.setAllocatedAddress(nodeAddress.getAddress());
				allocation.setAllocatedFrequencyBand(nodeAddress.getFrequencyBand());
				
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Node allocation: %s, %s => %s, %s.",
								nodeDeviceId, peerAddress, nodeLanId, new LoraAddress(allocation.getAllocatedAddress(),
										allocation.getAllocatedFrequencyBand())));
				}
				
				byte[] response = obmFactory.toBinary(allocation);
				communicator.send(introductedAddress, response);*/
				
				state = State.ALLOCATING;
				return;
			}
			
			if (!nodeAddress.equals(peerAddress)) {
				processParallelAddressConfigurationRequest(peerAddress);
			}
			
			if (state == State.ALLOCATING) {
				Confirmation confirmation = (Confirmation)obmFactory.toObject(Confirmation.class, data);
				
				if (!nodeDeviceId.equals(confirmation.getDeviceId())) {
					processParallelAddressConfigurationRequest(peerAddress);
				}
				
				confirm();
				state = State.CONFIRMING;
			}
		} catch (ProtocolException pe) {
			// TODO: handle exception
			System.out.println(pe);
		} catch (ClassCastException cce) {			
			// TODO: handle exception
			System.out.println(cce);
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
	public void setCommunicator(IDualLoraChipCommunicator communicator) {
		this.communicator = communicator;
	}
	
	@Override
	public void confirm() {
		concentrator.createNode(nodeDeviceId, new NodeAddress<LoraAddress>(CommunicationNet.LORA, nodeAddress));
/*		*/
	}
	
	public State getState() {
		return state;
	}
}
