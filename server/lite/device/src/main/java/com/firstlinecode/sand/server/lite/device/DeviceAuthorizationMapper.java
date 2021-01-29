package com.firstlinecode.sand.server.lite.device;

import com.firstlinecode.sand.server.device.DeviceAuthorization;

public interface DeviceAuthorizationMapper {
	void insert(DeviceAuthorization authorization);
	void updateCanceled(String deviceId, boolean canceled);
	DeviceAuthorization[] selectByDeviceId(String deviceId);
}
