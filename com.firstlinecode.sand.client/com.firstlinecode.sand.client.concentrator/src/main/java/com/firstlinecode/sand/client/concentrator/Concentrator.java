package com.firstlinecode.sand.client.concentrator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.RemoteServerTimeout;
import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.chalk.IChatServices;
import com.firstlinecode.chalk.ITask;
import com.firstlinecode.chalk.IUnidirectionalStream;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;
import com.firstlinecode.sand.protocols.concentrator.NodeCreated;

public class Concentrator implements IConcentrator {
	private static final String PATTERN_LAN_ID = "%02d";

	private static final Logger logger = LoggerFactory.getLogger(Concentrator.class);
	
	private static final int DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT = 1000 * 60 * 5;
	
	private List<IConcentrator.Listener> listeners;
	
	private String deviceId;
	private Map<String, Node> nodes;
	private Object nodesLock;
	
	private IChatServices chatServices;
	
	public Concentrator() {
		listeners = new ArrayList<>();
		nodes = new LinkedHashMap<>();
		nodesLock = new Object();
	}

	@Override
	public void init(String deviceId, Map<String, Node> nodes) {
		this.deviceId = deviceId;
		
		if (nodes == null || nodes.size() == 0)
			return;
		
		for (String lanId : nodes.keySet()) {
			this.nodes.put(lanId, nodes.get(lanId));
		}		
	}

	@Override
	public void addNode(final String deviceId, final NodeAddress<?> nodeAddress) {
		synchronized (nodesLock) {
			Node node = new Node();
			node.setDeviceId(deviceId);
			node.setLanId(getBestSuitedNewLanId());
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
						logger.error(String.format("Reduplicate device ID: %s.", node.getDeviceId()));
					}
					
					for (Listener listener : listeners) {
						listener.occurred(LanError.REDUPLICATE_DEVICE_ID, node);
					}
					
					return;
				}
				
				if (entry.getValue().getAddress().equals(node.getAddress())) {
					if (logger.isErrorEnabled()) {
						logger.error(String.format("Reduplicate device address: %s.", node.getAddress()));
					}
					
					for (Listener listener : listeners) {
						listener.occurred(LanError.REDUPLICATE_DEVICE_ADDRESS, node);
					}
					
					return;
				}
			}
			
			chatServices.getTaskService().execute(new NodeCreationTask(node));
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
			
			Iq iq = new Iq(createNode, Iq.Type.SET);
			stream.send(iq, DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT);
			
			synchronized (nodesLock) {				
				nodes.put(iq.getId(), node);
			}
		}

		@Override
		public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
			if (iq.getType() != Iq.Type.RESULT || iq.getObject() == null) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Server returns an bad response. Result is %s.", iq));
				}
				
				for (IConcentrator.Listener listener : listeners) {
					listener.occurred(IConcentrator.LanError.BAD_RESPONSE, node);
				}
				
				return;
			}
			
			NodeCreated nodeCreated = iq.getObject();
			
			Node confirmingNode = null;
			synchronized (nodesLock) {
				for (Node node : nodes.values()) {
					if (node.getDeviceId().equals(nodeCreated.getNode())) {
						confirmingNode = node;
						break;						
					}
				}
			}
			
			if (confirmingNode == null) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Confirming node which's device ID is '%s' not found.", nodeCreated.getNode()));
				}
				
				for (IConcentrator.Listener listener : listeners) {
					listener.occurred(IConcentrator.LanError.CONFIRMED_NODE_NOT_FOUND, node);
				}
				
				return;
			}
			
			if (!deviceId.equals(nodeCreated.getConcentrator()) ||
					nodeCreated.getLanId() == null ||
					nodeCreated.getMode() == null) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Bad node created response. Confirming node is %s and created node is %s.",
							getConfirmingNodeInfo(confirmingNode), getNodeCreatedInfo(nodeCreated)));
				}
				
				for (IConcentrator.Listener listener : listeners) {
					listener.occurred(IConcentrator.LanError.BAD_RESPONSE, node);
				}
				
				return;
			}
			
			if (!nodeCreated.getLanId().equals(confirmingNode.getLanId())) {
				for (String existedLanId : nodes.keySet()) {
					if (existedLanId.equals(nodeCreated.getLanId())) {
						if (logger.isErrorEnabled()) {
							logger.error(String.format("Server assigned a reduplicate LAN ID: %s.", nodeCreated.getLanId()));
						}
						
						for (IConcentrator.Listener listener : listeners) {
							listener.occurred(IConcentrator.LanError.SERVER_ASSIGNED_A_EXISTED_LAN_ID, node);
						}
						
						return;
					}
				}
				
			}
			
			confirmingNode.setMode(nodeCreated.getMode());
			
			synchronized (nodesLock) {
				nodes.remove(confirmingNode.getLanId());
				
				if (!confirmingNode.getLanId().equals(nodeCreated.getLanId())) {
					confirmingNode.setLanId(nodeCreated.getLanId());
				}
				
				confirmingNode.setConfirmed(true);
				nodes.put(confirmingNode.getLanId(), confirmingNode);
			}
			
			for (Listener listener : listeners) {
				listener.nodeAdded(confirmingNode.getLanId(), node);
			}
		}

		private Object getNodeCreatedInfo(NodeCreated nodeCreated) {
			// TODO Auto-generated method stub
			return null;
		}

		private Object getConfirmingNodeInfo(Node confirmingNode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Some errors occurred while creating node. Error defined condition is %s.",
						error.getDefinedCondition()));
			}
			
			for (IConcentrator.Listener listener : listeners) {
				listener.occurred(error, node);
			}
			
			return true;
		}

		@Override
		public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq iq) {
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Timeout on node[%s, %s] creation.",
						node.getDeviceId(), node.getLanId()));
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
		int i = 1;
		String lanId = null;
		for (String currentLanId : nodes.keySet()) {
			if (lanId == null && Integer.parseInt(currentLanId) != i) {
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
