package com.firstlinecode.sand.server.concentrator;

public interface IConcentrator {
	boolean containsNode(String deviceId);
	void requestConfirmation(NodeConfirmationRequest request);
	void confirm(String deviceId);
	String createNode(Node node);
	Node[] getNodes(String deviceId);
}
