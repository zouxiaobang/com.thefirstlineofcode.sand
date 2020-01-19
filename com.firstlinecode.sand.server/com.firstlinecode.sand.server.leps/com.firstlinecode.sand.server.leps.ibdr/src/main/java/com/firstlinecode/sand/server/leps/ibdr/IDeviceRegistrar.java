package com.firstlinecode.sand.server.leps.ibdr;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceRegistrar {
	DeviceIdentity register(String deviceId);
	void remove(String deviceId);
}
