package com.firstlinecode.sand.server.lite.device;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface DeviceIdentityMapper {
	void insert(DeviceIdentity identity);
	DeviceIdentity selectByDeviceName(String deviceName);
	int selectCountByDeviceName(String deviceName);
}
