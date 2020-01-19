package com.firstlinecode.sand.server.lite.auth;

import java.util.Date;

import com.firstlinecode.sand.server.framework.auth.DeviceAuthorization;

public interface DeviceAuthorizationMapper {
	void insert(String deviceId, String authorizer, Date authorizeTime, Date expiredTime);
	void updateCanceled(String deviceId, boolean canceled);
	DeviceAuthorization[] selectByDeviceId(String deviceId);
}
