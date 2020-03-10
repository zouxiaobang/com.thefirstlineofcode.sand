package com.firstlinecode.sand.server.framework.devices.concentrator;

public interface IConcentrator {
	String requestNodeCreation(Node node);
	String confirmNodeCreation(String deviceId);
	String[] getNodes();
}
