package com.thefirstlineofcode.sand.server.concentrator;

public interface IConcentratorFactory {
	boolean isConcentrator(String deviceId);
	IConcentrator getConcentrator(String deviceId);
	String getConcentratorDeviceNameByNodeDeviceId(String nodeDeviceId);
	boolean isLanNode(String deviceId);
}
