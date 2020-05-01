package com.firstlinecode.sand.server.concentrator;

public interface IConcentrator {
	boolean containsNode(String nodeDeviceId);
	boolean containsLanId(String lanId);
	void requestConfirmation(NodeConfirmation confirmation);
	void cancelConfirmation(String nodeDeviceId);
	Confirmed confirm(String nodeDeviceId, String confirmer);
	Node getNode(String lanId);
	Node[] getNodes();
}
