package com.firstlinecode.sand.server.framework.things.concentrator;

public interface IConcentrator {
	boolean containsNode(String deviceId);
	void confirm(String deviceId);
	String createNode(Node node);
	Node[] getNodes();
}
