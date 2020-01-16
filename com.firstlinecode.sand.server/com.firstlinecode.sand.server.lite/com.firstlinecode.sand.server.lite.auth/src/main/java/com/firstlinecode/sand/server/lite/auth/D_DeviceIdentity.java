package com.firstlinecode.sand.server.lite.auth;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.core.auth.DeviceIdentityState;

public class D_DeviceIdentity extends DeviceIdentity implements IIdProvider<String> {
	private String id;
	private String deviceId;
	private DeviceIdentityState state;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public DeviceIdentityState getState() {
		return state;
	}

	public void setState(DeviceIdentityState state) {
		this.state = state;
	}
}
