package com.thefirstlineofcode.sand.server.lite.devices;

import com.thefirstlineofcode.sand.server.devices.DeviceAuthorization;

public interface DeviceAuthorizationMapper {
	void insert(DeviceAuthorization authorization);
	void updateCanceled(String deviceId, boolean canceled);
	DeviceAuthorization[] selectByDeviceId(String deviceId);
}
