package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.sand.protocols.ibdr.DeviceIdentity;

public interface IDeviceRegistrar {
	DeviceIdentity register(String deviceId);
	void remove(String deviceId);
}
