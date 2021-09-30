package com.firstlinecode.sand.server.lite.devices;

import com.firstlinecode.sand.server.devices.Device;
import com.thefirstlineofcode.basalt.protocol.core.JabberId;

public interface DeviceMapper {
	void insert(Device device);
	void delete(JabberId jid);
	Device selectByDeviceId(String deviceId);
	Device selectByDeviceName(String deviceName);
	int selectCountByDeviceId(String deviceId);
	int selectCountByDeviceName(String deviceName);
}
