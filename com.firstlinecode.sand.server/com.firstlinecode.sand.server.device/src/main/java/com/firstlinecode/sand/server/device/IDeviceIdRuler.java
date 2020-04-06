package com.firstlinecode.sand.server.device;

public interface IDeviceIdRuler {
	boolean isValid(String deviceId);
	String guessMode(String deviceId);
}
