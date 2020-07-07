package com.firstlinecode.sand.server.ibdr;

public interface IDeviceRegistrar {
	DeviceRegistrationEvent register(String deviceId);
	void remove(String deviceId);
}
