package com.firstlinecode.sand.server.framework.things;

public interface IDeviceIdRuler {
	boolean isValid(String deviceId);
	String guessMode(String deviceId);
}
