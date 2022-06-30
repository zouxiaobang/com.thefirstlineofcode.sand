package com.thefirstlineofcode.sand.client.concentrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlinelinecode.sand.protocols.concentrator.CreateNode;
import com.thefirstlinelinecode.sand.protocols.concentrator.NodeAddress;
import com.thefirstlinelinecode.sand.protocols.concentrator.NodeCreated;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.RemoteServerTimeout;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.ITask;
import com.thefirstlineofcode.chalk.core.IUnidirectionalStream;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.core.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.client.core.concentrator.IModelRegistrar;
import com.thefirstlineofcode.sand.client.core.concentrator.Node;
import com.thefirstlineofcode.sand.protocols.core.CommunicationNet;

public class Concentrator implements IConcentrator {
	private static final String PATTERN_LAN_ID = "%02d";

	private static final Logger logger = LoggerFactory.getLogger(Concentrator.class);
	
	private static final int DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT = 1000 * 30;
	
	private List<IConcentrator.Listener> listeners;
	
	private String deviceName;
	private Map<String, Node> nodes;
	private Object nodesLock;
	
	private IModelRegistrar modelRegistrar;
	private Map<CommunicationNet, ? extends ICommunicator<?, ?, ?>> communicators; 
	
	private IChatServices chatServices;
	
	public Concentrator() {
		listeners = new ArrayList<>();
		nodes = new LinkedHashMap<>();
		nodesLock = new Object();
	}

	@Override
	public void init(String deviceName, Map<String, Node> nodes, IModelRegistrar modelRegistrar, Map<CommunicationNet, ? extends ICommunicator<?, ?, ?>> communicators) {
		this.deviceName = deviceName;
		this.modelRegistrar = modelRegistrar;
		this.communicators = communicators;
		
		if (nodes == null || nodes.size() == 0)
			return;
		
		for (String lanId : nodes.keySet()) {
			this.nodes.put(lanId, nodes.get(lanId));
		}
		
	}

	@Override
	public void createNode(String deviceId, String requestLanId, NodeAddress<?> nodeAddress) {
		synchronized (nodesLock) {
			Node node = new Node();
			node.setDeviceId(deviceId);
			node.setLanId(requestLanId);
			node.setCommunicationNet(nodeAddress.getCommunicationNet());
			node.setAddress(nodeAddress.getAddress());
			node.setConfirmed(false);
			
			if (nodes.size() > 99) {
				if (logger.isErrorEnabled()) {
					logger.error("Node size overflow.");
				}
				
				for (Listener listener : listeners) {
					listener.occurred(LanError.SIZE_OVERFLOW, node);
				}
				
				return;
			}
			
			for (Entry<String, Node> entry : nodes.entrySet()) {
				if (entry.getValue().getDeviceId().equals(node.getDeviceId())) {
					if (logger.isErrorEnabled()) {
						logger.error("Reduplicate device ID: {}.", node.getDeviceId());
					}
					
					for (Listener listener : listeners) {
						listener.occurred(LanError.REDUPLICATE_DEVICE_ID, node);
					}
					
					return;
				}
				
				if (entry.getValue().getAddress().equals(node.getAddress())) {
					if (logger.isErrorEnabled()) {
						logger.error("Reduplicate device address: {}.", node.getAddress());
					}
					
					for (Listener listener : listeners) {
						listener.occurred(LanError.REDUPLICATE_DEVICE_ADDRESS, node);
					}
					
					return;
				}
				
				if (entry.getValue().getLanId().equals(node.getLanId())) {
					if (logger.isErrorEnabled()) {
						logger.error("Reduplicate device LAN ID: {}.", node.getLanId());
					}
					
					for (Listener listener : listeners) {
						listener.occurred(LanError.REDUPLICATE_DEVICE_ADDRESS, node);
					}
					
					return;
				}
			}
			
			chatServices.getTaskService().execute(new NodeCreationTask(node));
			
			if (logger.isDebugEnabled()) {
				logger.debug("Node creation request for node which's deviceID is '{}' and address is '{}' has sent.",
						deviceId, nodeAddress);
			}
		}
	}
	
	private class NodeCreationTask implements ITask<Iq> {
		private Node node;
		
		public NodeCreationTask(Node node) {
			this.node = node;
		}

		@Override
		public void trigger(IUnidirectionalStream<Iq> stream) {
			CreateNode createNode = new CreateNode();
			createNode.setDeviceId(node.getDeviceId());
			createNode.setLanId(node.getLanId());
			createNode.setCommunicationNet(node.getCommunicationNet().toString());
			createNode.setAddress(node.getAddress());
			
			Iq iq = new Iq(Iq.Type.SET, createNode);
			stream.send(iq, DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT);
			
			synchronized (nodesLock) {
				nodes.put(iq.getId(), node);
			}
		}

		@Override
		public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
			if (iq.getType() != Iq.Type.RESULT || iq.getObject() == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Server returns a bad response. Result is {}.", iq);
				}
				
				for (IConcentrator.Listener listener : listeners) {
					listener.occurred(IConcentrator.LanError.BAD_NODE_CREATED_RESPONSE, node);
				}
				
				return;
			}
			
			NodeCreated nodeCreated = iq.getObject();
			
			Node confirmingNode = nodes.get(iq.getId());
			if (confirmingNode == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Confirming node which's device ID is '{}' not found.", nodeCreated.getNodeDeviceId());
				}
				
				for (IConcentrator.Listener listener : listeners) {
					listener.occurred(IConcentrator.LanError.CREATED_NODE_NOT_FOUND, node);
				}
				
				return;
			}
			
			if (!deviceName.equals(nodeCreated.getConcentratorDeviceName()) ||
					!nodeCreated.getNodeDeviceId().equals(confirmingNode.getDeviceId()) ||
					nodeCreated.getLanId() == null ||
					nodeCreated.getModel() == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Bad node created response. Device name of concentrator is {}. Device ID of confirming node is {} and Device ID of created node is {}.",
							deviceName, confirmingNode.getDeviceId(), nodeCreated.getNodeDeviceId());
				}
				
				for (IConcentrator.Listener listener : listeners) {
					listener.occurred(IConcentrator.LanError.BAD_NODE_CREATED_RESPONSE, node);
				}
				
				return;
			}
			
			if (!nodeCreated.getLanId().equals(confirmingNode.getLanId())) {
				for (String existedLanId : nodes.keySet()) {
					if (existedLanId.equals(nodeCreated.getLanId())) {
						if (logger.isErrorEnabled()) {
							logger.error("Server assigned a reduplicate LAN ID: {}.", nodeCreated.getLanId());
						}
						
						for (IConcentrator.Listener listener : listeners) {
							listener.occurred(IConcentrator.LanError.SERVER_ASSIGNED_A_EXISTED_LAN_ID, node);
						}
						
						return;
					}
				}
				
			}
			
			confirmingNode.setModel(nodeCreated.getModel());
			
			String requestedLanId = confirmingNode.getLanId();
			synchronized (nodesLock) {
				nodes.remove(iq.getId());
				
				if (!requestedLanId.equals(nodeCreated.getLanId())) {
					confirmingNode.setLanId(nodeCreated.getLanId());
				}
				
				confirmingNode.setConfirmed(true);
				nodes.put(confirmingNode.getLanId(), confirmingNode);
			}
			
			for (Listener listener : listeners) {
				listener.nodeCreated(requestedLanId, nodeCreated.getLanId(), node);
			}
		}
		
		@Override
		public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
			if (logger.isErrorEnabled()) {
				logger.error("Some errors occurred while creating node. Error defined condition: '{}'. Error text: '{}'.",
						error.getDefinedCondition(), error.getText());
			}
			
			try {
				for (IConcentrator.Listener listener : listeners) {
					listener.occurred(error, node);
				}				
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Exception was thrown while processing error.", e);
				}
			} finally {				
				synchronized (nodesLock) {
					nodes.remove(error.getId());
				}
			}
			
			return true;
		}

		@Override
		public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq iq) {
			if (logger.isErrorEnabled()) {
				logger.error("Timeout on node[{}, {}] creation.", node.getDeviceId(), node.getLanId());
			}
			
			nodes.remove(node.getLanId());
			
			for (IConcentrator.Listener listener : listeners) {
				listener.occurred(new RemoteServerTimeout(), node);
			}
			
			return true;
		}

		@Override
		public void interrupted() {
			// NO-OP
		}
		
	}

	@Override
	public void removeNode(String lanId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Map<String, Node> getNodes() {
		return nodes;
	}

	@Override
	public void addListener(IConcentrator.Listener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public IConcentrator.Listener removeListener(IConcentrator.Listener listener) {
		 if (listeners.remove(listener))
			 return listener;
		 
		 return null;
	}

	@Override
	public Node getNode(String lanId) {
		return nodes.get(lanId);
	}

	@Override
	public Node[] pullNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBestSuitedNewLanId() {
		synchronized (nodesLock) {
			List<String > lanIds = new ArrayList<>();
			for (Node node : nodes.values()) {
				if (node.isConfirmed()) {
					lanIds.add(node.getLanId());
				}
			}
			
			Collections.sort(lanIds);
			
			int i = 1;
			String lanId = null;
			for (String nodeLanId : lanIds) {
				if (Integer.parseInt(nodeLanId) != i) {
					lanId = String.format(PATTERN_LAN_ID, i);
				}
				
				i++;
			}
			
			if (lanId == null) {
				lanId = String.format(PATTERN_LAN_ID, i);
			}
			
			return lanId;
		}
	}

	@Override
	public ICommunicator<?, ?, ?> getCommunicator(CommunicationNet communicationNet) {
		return communicators.get(communicationNet);
	}

	@Override
	public String getDeviceName() {
		return deviceName;
	}

	@Override
	public IModelRegistrar getModeRegistrar() {
		return modelRegistrar;
	}

}
