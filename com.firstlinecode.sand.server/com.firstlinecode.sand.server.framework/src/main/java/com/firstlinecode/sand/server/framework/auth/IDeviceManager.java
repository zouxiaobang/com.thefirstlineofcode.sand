package com.firstlinecode.sand.server.framework.auth;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceManager {
	void authorize(String deviceId, String authorizer, long validityTime);
	void cancelAuthorization(String deviceId);
	DeviceIdentity register(String deviceId);
	void remove(JabberId jid);
	boolean exists(String deviceId);
	boolean exists(JabberId jid);
	DeviceIdentity get(String deviceId);
	DeviceIdentity get(JabberId jid);
}
