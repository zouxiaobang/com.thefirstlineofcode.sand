package com.thefirstlineofcode.sand.server.ibdr;

import com.thefirstlineofcode.sand.server.devices.DeviceRegistered;

public interface IDeviceRegistrar {
	DeviceRegistered register(String deviceId);
	void remove(String deviceId);
}
