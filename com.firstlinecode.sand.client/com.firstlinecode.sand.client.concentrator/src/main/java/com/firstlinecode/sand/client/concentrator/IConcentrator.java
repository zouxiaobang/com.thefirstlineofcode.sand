package com.firstlinecode.sand.client.concentrator;

import java.util.Map;

import com.firstlinecode.basalt.protocol.core.IError;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;

public interface IConcentrator {
	enum LanError {
		SIZE_OVERFLOW,
		DEREPULICATED_DEVICE_ID,
		DEREPULICATED_DEVICE_ADDRESS
	}
	void init(Map<String, Node> nodes);
	String getBestSuitedNewLanId();
	void addNode(String deviceId, NodeAddress<?> address);
	void removeNode(String lanId);
	Map<String, Node> getNodes();
	Node getNode(String lanId);
	Node[] pullNodes();
	void addListener(IConcentrator.Listener listener);
	IConcentrator.Listener removeListener(IConcentrator.Listener listener);
	
	public interface Listener {
		void nodeAdded(String lanId, Node node);
		void nodeRemoved(String lanId, Node node);
		void occurred(IError error, Node source);
		void occurred(LanError error, Node source);
	}
}
