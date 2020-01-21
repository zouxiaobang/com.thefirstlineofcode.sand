package com.firstlinecode.sand.server.lite.auth;

import com.firstlinecode.sand.server.framework.auth.DeviceAuthorization;

public interface DeviceAuthorizationMapper {
	void insert(DeviceAuthorization authorization);
	void updateCanceled(String deviceId, boolean canceled);
	DeviceAuthorization[] selectByDeviceId(String deviceId);
}
