package com.firstlinecode.sand.server.core.auth;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceIdentityManager {
	void authorize(String deviceId);
	DeviceIdentity register(String deviceId);
	void remove(JabberId jid);
	void remove(String deviceId);
	boolean exists(JabberId jid);
	boolean exists(String deviceId);
	DeviceIdentity get(JabberId jid);
	DeviceIdentity get(String deviceId);
}
