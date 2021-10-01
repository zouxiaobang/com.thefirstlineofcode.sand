package com.thefirstlineofcode.sand.server.lite.devices;

import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public interface DeviceIdentityMapper {
	void insert(DeviceIdentity identity);
	DeviceIdentity selectByDeviceName(String deviceName);
	DeviceIdentity selectByDeviceId(String deviceId);
	int selectCountByDeviceName(String deviceName);
}
