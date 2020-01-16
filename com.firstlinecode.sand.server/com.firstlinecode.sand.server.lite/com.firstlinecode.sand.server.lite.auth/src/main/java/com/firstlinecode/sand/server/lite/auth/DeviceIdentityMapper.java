package com.firstlinecode.sand.server.lite.auth;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.core.auth.DeviceIdentityState;

public interface DeviceIdentityMapper {
	void insert(D_DeviceIdentity deviceIdentity);
	void updateDeviceIdentityState(String deviceId, DeviceIdentityState state);
	void delete(JabberId jid);
	DeviceIdentity selectByJid(JabberId jid);
	int selectCountByJid(JabberId jid);
}
