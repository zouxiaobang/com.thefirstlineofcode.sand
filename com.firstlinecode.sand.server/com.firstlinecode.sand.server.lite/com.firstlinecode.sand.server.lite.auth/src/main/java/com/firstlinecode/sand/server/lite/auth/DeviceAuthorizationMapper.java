package com.firstlinecode.sand.server.lite.auth;

import java.util.Date;

public interface DeviceAuthorizationMapper {
	void insert(String deviceId, Date authorizeTime, Date expiredTime);
	
}
