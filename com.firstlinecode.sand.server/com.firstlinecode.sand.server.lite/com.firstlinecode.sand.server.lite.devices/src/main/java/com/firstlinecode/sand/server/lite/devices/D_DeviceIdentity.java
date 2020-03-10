package com.firstlinecode.sand.server.lite.devices;

import java.util.Date;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class D_DeviceIdentity extends DeviceIdentity implements IIdProvider<String> {
	private String id;
	private String deviceId;
	private Date registeredTime;
	private String authorizationId;
	
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

	public Date getRegisteredTime() {
		return registeredTime;
	}

	public void setRegisteredTime(Date registerTime) {
		this.registeredTime = registerTime;
	}

	public String getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(String authorizationId) {
		this.authorizationId = authorizationId;
	}
	
}
