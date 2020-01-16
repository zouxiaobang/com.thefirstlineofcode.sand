package com.firstlinecode.sand.server.lite.auth;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.core.auth.IDeviceIdentityManager;

public class DeviceIdentityManager implements IDeviceIdentityManager {

	@Override
	public void authorize(String deviceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public DeviceIdentity register(String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(JabberId jid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(String deviceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exists(JabberId jid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(String deviceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DeviceIdentity get(JabberId jid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeviceIdentity get(String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
