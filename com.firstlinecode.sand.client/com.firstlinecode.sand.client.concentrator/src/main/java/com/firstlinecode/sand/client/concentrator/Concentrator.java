package com.firstlinecode.sand.client.concentrator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.chalk.IChatServices;
import com.firstlinecode.chalk.ITask;
import com.firstlinecode.chalk.IUnidirectionalStream;
import com.firstlinecode.sand.client.concentrator.IConcentrator.Listener.LanError;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;
import com.firstlinecode.sand.protocols.core.CommunicationNet;

public class Concentrator implements IConcentrator {
	private static final String PATTERN_LAN_ID = "%02d";

	private static final Logger logger = LoggerFactory.getLogger(Concentrator.class);
	
	private static final int DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT = 1000 * 60 * 2;
	
	private List<IConcentrator.Listener> listeners;
	private Map<String, Node> nodes;
	private Object nodesLock;
	
	private IChatServices chatServices;
	
	public Concentrator() {
		listeners = new ArrayList<>();
		nodes = new LinkedHashMap<>();
		nodesLock = new Object();
	}

	@Override
	public void init(Map<String, Node> nodes) {
		if (nodes == null || nodes.size() == 0)
			return;
		
		for (String lanId : nodes.keySet()) {
			this.nodes.put(lanId, nodes.get(lanId));
		}
	}

	@Override
	public void createNode(final String deviceId, final NodeAddress<?> nodeAddress) {
		synchronized (nodesLock) {
			Node node = new Node();
			node.setDeviceId(deviceId);
			node.setAddress(nodeAddress.getAddress());
			
			if (nodes.size() > 99) {	
				for (Listener listener : listeners) {
					listener.occurred(LanError.SIZE_OVERFLOW, node);
				}
				
				return;
			}
			
			int i = 1;
			String lanId = null;
			for (Entry<String, Node> entry : nodes.entrySet()) {
				if (entry.getValue().getDeviceId().equals(node.getDeviceId())) {
					for (Listener listener : listeners) {
						listener.occurred(LanError.DEREPULICATED_DEVICE_ID, node);
					}
					
					return;
				}
				
				if (entry.getValue().getAddress().equals(node.getAddress())) {
					for (Listener listener : listeners) {
						listener.occurred(LanError.DEREPULICATED_DEVICE_ADDRESS, node);
					}
					
					return;
				}
				
				String currentLanId = entry.getKey();
				if (lanId == null && Integer.parseInt(currentLanId) != i) {
					lanId = String.format(PATTERN_LAN_ID, i);
				}
				
				i++;
			}
			
			if (lanId == null) {
				lanId = String.format(PATTERN_LAN_ID, i);
			}
			
			chatServices.getTaskService().execute(new NodeCreationTask(lanId, nodeAddress.getCommunicationNet(), node));
		}
	}
	
	private class NodeCreationTask implements ITask<Iq> {
		private String lanId;
		private CommunicationNet communicationNet;
		private Node node;
		
		public NodeCreationTask(String lanId, CommunicationNet communicationNet, Node node) {
			this.lanId = lanId;
			this.communicationNet = communicationNet;
			this.node = node;
		}

		@Override
		public void trigger(IUnidirectionalStream<Iq> stream) {
			CreateNode createNode = new CreateNode();
			createNode.setDeviceId(node.getDeviceId());
			createNode.setCommunicationNet(communicationNet.toString());
			createNode.setAddress(node.getAddress());
			createNode.setLanId(lanId);
			
			Iq iq = new Iq(Iq.Type.SET, "cn-" + lanId);
			iq.setObject(createNode);
			
			stream.send(iq, DEFAULT_ADDRESS_CONFIGURATION_NODE_CREATION_TIMEOUT);
		}

		@Override
		public void processResponse(IUnidirectionalStream<Iq> stream, Iq stanza) {
			nodes.put(lanId, node);
			
			for (Listener listener : listeners) {
				listener.created(lanId, node);
			}
		}

		@Override
		public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Some errors occurred while creating node. Error defined condition is %s.",
						error.getDefinedCondition()));
			}
			
			return true;
		}

		@Override
		public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq stanza) {
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Timeout on node[%s, %s] creation.",
						node.getAddress(), lanId));
			}
			
			return true;
		}

		@Override
		public void interrupted() {
			// No-Op
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

}
