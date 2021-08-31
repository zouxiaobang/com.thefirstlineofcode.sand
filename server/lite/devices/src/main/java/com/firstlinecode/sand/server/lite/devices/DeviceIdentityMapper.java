package com.firstlinecode.sand.server.lite.devices;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface DeviceIdentityMapper {
	void insert(DeviceIdentity identity);
	DeviceIdentity selectByDeviceName(String deviceName);
	DeviceIdentity selectByDeviceId(String deviceId);
	int selectCountByDeviceName(String deviceName);
}
