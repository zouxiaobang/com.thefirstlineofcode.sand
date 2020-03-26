package com.firstlinecode.sand.server.framework.things.concentrator;

public interface IConcentrator {
	String createNode(Node node);
	String confirm(String deviceId);
	String[] getNodes();
}
