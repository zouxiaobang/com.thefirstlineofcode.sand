package com.firstlinecode.sand.client.lora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.ITask;
import com.firstlinecode.chalk.IUnidirectionalStream;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IObmFactory;
import com.firstlinecode.sand.client.things.commuication.ObmFactory;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.client.things.concentrator.Node;
import com.firstlinecode.sand.client.things.concentrator.NodeCreationException;
import com.firstlinecode.sand.client.things.concentrator.NodeNotFoundException;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.protocols.lora.dac.Allocation;
import com.firstlinecode.sand.protocols.lora.dac.Confirmation;
import com.firstlinecode.sand.protocols.lora.dac.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<IDualLoraChipCommunicator, LoraAddress, byte[]> {
	private static final Logger logger = LoggerFactory.getLogger(DynamicAddressConfigurator.class);
	
	private static final DualLoraAddress ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS = new DualLoraAddress(
			LoraAddress.MAX_TWO_BYTES_ADDRESS, DualLoraAddress.MAX_CHANNEL);
	private static final int DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT = 1000 * 60 * 2;
	
	public enum State {
		WORKING,
		WAITING,
		ALLOCATING,
		CONFIRMING
	}
	
	private IConcentrator<LoraAddress> concentrator;
	private IDualLoraChipCommunicator communicator;
	private DualLoraAddress workingAddress;
	private String nodeDeviceId;
	private LoraAddress nodeAddress;
	private String nodeLanId;
	
	private IObmFactory obmFactory;
	private IChatClient chatClient;
	
	private State state;
	
	public DynamicAddressConfigurator(IConcentrator<LoraAddress> concentrator,
			IDualLoraChipCommunicator communicator, IChatClient chatClient) {
		this.concentrator = concentrator;
		this.communicator = communicator;
		this.chatClient = chatClient;
		
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
				
				int nodesSize = concentrator.getLanIds().length;
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
				communicator.send(introductedAddress, response);
				
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
		chatClient.getChatServices().getTaskService().execute(new ITask<Iq>() {

			@Override
			public void trigger(IUnidirectionalStream<Iq> stream) {
				try {
					concentrator.createNode(new Node<LoraAddress>(nodeDeviceId, nodeAddress));
				} catch (NodeCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				CreateNode<LoraAddress> createNode = new CreateNode<>();
				createNode.setDeviceId(nodeDeviceId);
				createNode.setAddress(nodeAddress);
				createNode.setLanId(nodeLanId);
				
				Iq iq = new Iq(Iq.Type.SET, "nc-" + nodeLanId);
				iq.setObject(createNode);
				
				stream.send(iq, DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT);
			}
			
			@Override
			public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
				try {
					concentrator.setNodeEnabled(nodeLanId, true);
				} catch (NodeNotFoundException e) {
					// ???
					String errorMsg = String.format("Node which's lan ID is %s not found.", nodeLanId);
					if (logger.isErrorEnabled()) {
						logger.error(errorMsg);
					}
					
					throw new RuntimeException(errorMsg);
				}
			}
			
			@Override
			public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
				String errorMsg = String.format("Some errors occurred while creating node. Error defined condition is %s.",
						error.getDefinedCondition());
				if (logger.isErrorEnabled()) {
					logger.error(errorMsg);
				}
				
				throw new RuntimeException(errorMsg);
			}
			
			@Override
			public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq stanza) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Timeout on node[%s, %s] creation.",
							communicator.getAddress(), nodeLanId));
				}
				
				return true;
			}

			@Override
			public void interrupted() {
				// No-Op
			}
			
		});
	}
	
	public State getState() {
		return state;
	}
}
