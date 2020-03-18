package com.firstlinecode.sand.client.concentrator;

import java.util.Map;

import com.firstlinecode.basalt.protocol.core.IError;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;

public interface IConcentrator {
	void init(Map<String, Node> nodes);
	void createNode(String deviceId, NodeAddress<?> address);
	void removeNode(String lanId);
	Map<String, Node> getNodes();
	Node getNode(String lanId);
	void addListener(IConcentrator.Listener listener);
	IConcentrator.Listener removeListener(IConcentrator.Listener listener);
	
	public interface Listener {
		public enum LanError {
			DEREPULICATED_DEVICE_ID,
			DEREPULICATED_DEVICE_ADDRESS,
			SIZE_OVERFLOW
		}
		
		void created(String lanId, Node node);
		void removed(String lanId, Node node);
		void occurred(LanError error, Node source);
		void occurred(IError error, Node source);
	}
}
