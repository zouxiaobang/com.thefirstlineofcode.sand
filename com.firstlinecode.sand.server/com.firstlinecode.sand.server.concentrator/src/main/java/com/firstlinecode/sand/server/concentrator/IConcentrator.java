package com.firstlinecode.sand.server.concentrator;

public interface IConcentrator {
	boolean containsNode(String nodeDeviceId);
	void requestConfirmation(NodeConfirmation confirmation);
	void cancelAuthorization(String nodeDeviceId);
	void confirm(String nodeDeviceId, String confirmer);
	Node[] getNodes();
}
