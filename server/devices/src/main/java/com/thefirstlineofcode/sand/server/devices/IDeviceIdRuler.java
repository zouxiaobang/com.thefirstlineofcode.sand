package com.thefirstlineofcode.sand.server.devices;

public interface IDeviceIdRuler {
	boolean isValid(String deviceId);
	String guessModel(String deviceId);
}
