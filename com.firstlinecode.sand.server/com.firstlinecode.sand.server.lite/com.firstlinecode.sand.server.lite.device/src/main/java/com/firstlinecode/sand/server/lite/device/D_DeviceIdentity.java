package com.firstlinecode.sand.server.lite.device;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class D_DeviceIdentity extends DeviceIdentity implements IIdProvider<String> {
	private static final long serialVersionUID = 6554323836858199250L;
	
	private String id;
	private String deviceId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
}
