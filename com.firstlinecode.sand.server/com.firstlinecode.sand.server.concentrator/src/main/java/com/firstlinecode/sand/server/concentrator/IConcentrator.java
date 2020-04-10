package com.firstlinecode.sand.server.concentrator;

public interface IConcentrator {
	boolean containsNode(String nodeDeviceId);
	void requestConfirmation(NodeConfirmation confirmation);
	void confirm(String nodeDeviceId);
	Node[] getNodes();
}
