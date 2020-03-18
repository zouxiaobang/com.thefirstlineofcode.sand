package com.firstlinecode.sand.server.framework.things.concentrator;

public interface IConcentrator {
	String requestNodeCreation(Node node);
	String confirmNodeCreation(String deviceId);
	String[] getNodes();
}
