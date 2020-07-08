package com.firstlinecode.sand.client.things.concentrator;

import java.util.Map;

import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;
import com.firstlinecode.sand.protocols.core.CommunicationNet;

public interface IConcentrator {
	public static final String LAN_ID_CONCENTRATOR = "00";
	
	public enum LanError {
		SIZE_OVERFLOW,
		REDUPLICATE_DEVICE_ID,
		REDUPLICATE_DEVICE_ADDRESS,
		BAD_NODE_CREATED_RESPONSE,
		CREATED_NODE_NOT_FOUND,
		SERVER_ASSIGNED_A_EXISTED_LAN_ID
	}
	
	void init(String deviceId, Map<String, Node> nodes, Map<CommunicationNet, ? extends ICommunicator<?, ?, ?>> communicators);
	String getBestSuitedNewLanId();
	void createNode(String deviceId, String lanId, NodeAddress<?> address);
	void removeNode(String lanId);
	Map<String, Node> getNodes();
	Node getNode(String lanId);
	Node[] pullNodes();
	ICommunicator<?, ?, ?> getCommunicator(CommunicationNet communicationNet);
	String getDeviceId();
	void addListener(IConcentrator.Listener listener);
	IConcentrator.Listener removeListener(IConcentrator.Listener listener);
	
	public interface Listener {
		void nodeCreated(String requestedLanId, String allocatedLanId, Node node);
		void nodeRemoved(String lanId, Node node);
		void occurred(StanzaError error, Node source);
		void occurred(LanError error, Node source);
	}
}
