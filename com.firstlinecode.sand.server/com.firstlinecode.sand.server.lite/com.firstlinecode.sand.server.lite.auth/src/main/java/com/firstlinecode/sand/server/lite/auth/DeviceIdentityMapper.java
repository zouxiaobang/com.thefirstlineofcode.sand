package com.firstlinecode.sand.server.lite.auth;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface DeviceIdentityMapper {
	void insert(D_DeviceIdentity deviceIdentity);
	void delete(JabberId jid);
	DeviceIdentity selectByDeviceId(String deviceId);
	DeviceIdentity selectByDeviceName(String deviceName);
	int selectCountByDeviceId(String deviceId);
	int selectCountByDeviceName(String deviceName);
}
