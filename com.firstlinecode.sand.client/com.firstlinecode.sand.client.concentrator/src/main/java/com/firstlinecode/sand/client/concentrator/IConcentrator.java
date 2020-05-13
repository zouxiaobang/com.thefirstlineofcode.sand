package com.firstlinecode.sand.client.concentrator;

import java.util.Map;

import com.firstlinecode.basalt.protocol.core.IError;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;
import com.firstlinecode.sand.protocols.core.CommunicationNet;

public interface IConcentrator {
	public static final String LAN_ID_CONCENTRATOR = "00";
	
	public enum LanError {
		SIZE_OVERFLOW,
		REDUPLICATE_DEVICE_ID,
		REDUPLICATE_DEVICE_ADDRESS,
		BAD_RESPONSE,
		CONFIRMED_NODE_NOT_FOUND,
		SERVER_ASSIGNED_A_EXISTED_LAN_ID
	}
	
	void init(String deviceId, Map<String, Node> nodes, Map<CommunicationNet, ? extends ICommunicator<?, ?, ?>> communicators);
	String getBestSuitedNewLanId();
	void addNode(String deviceId, NodeAddress<?> address);
	void removeNode(String lanId);
	Map<String, Node> getNodes();
	Node getNode(String lanId);
	Node[] pullNodes();
	ICommunicator<?, ?, ?> getCommunicator(CommunicationNet communicationNet);
	String getDeviceId();
	void addListener(IConcentrator.Listener listener);
	IConcentrator.Listener removeListener(IConcentrator.Listener listener);
	
	public interface Listener {
		void nodeAdded(String lanId, Node node);
		void nodeRemoved(String lanId, Node node);
		void occurred(IError error, Node source);
		void occurred(LanError error, Node source);
	}
}
