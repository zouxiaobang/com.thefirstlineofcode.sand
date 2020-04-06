package com.firstlinecode.sand.server.lite.device;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.server.device.Device;

public interface DeviceMapper {
	void insert(Device device);
	void delete(JabberId jid);
	Device selectByDeviceId(String deviceId);
	Device selectByDeviceName(String deviceName);
	int selectCountByDeviceId(String deviceId);
	int selectCountByDeviceName(String deviceName);
}
