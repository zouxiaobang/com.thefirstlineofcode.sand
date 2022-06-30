package com.thefirstlineofcode.sand.server.lite.devices;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.sand.server.devices.Device;

public interface DeviceMapper {
	void insert(Device device);
	void delete(JabberId jid);
	Device selectByDeviceId(String deviceId);
	Device selectByDeviceName(String deviceName);
	int selectCountByDeviceId(String deviceId);
	int selectCountByDeviceName(String deviceName);
}
