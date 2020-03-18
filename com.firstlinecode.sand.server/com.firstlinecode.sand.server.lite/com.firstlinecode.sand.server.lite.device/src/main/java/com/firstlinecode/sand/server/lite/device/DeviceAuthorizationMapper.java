package com.firstlinecode.sand.server.lite.device;

import com.firstlinecode.sand.server.framework.things.DeviceAuthorization;

public interface DeviceAuthorizationMapper {
	void insert(DeviceAuthorization authorization);
	void updateCanceled(String deviceId, boolean canceled);
	DeviceAuthorization[] selectByDeviceId(String deviceId);
}
