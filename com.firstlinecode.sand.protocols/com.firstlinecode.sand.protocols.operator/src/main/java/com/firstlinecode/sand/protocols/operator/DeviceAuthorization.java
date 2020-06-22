package com.firstlinecode.sand.protocols.operator;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace = "http://firstlinecode.com/sand-demo", localName = "auth")
public class DeviceAuthorization {
	private String deviceId;
	private boolean canceled;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
}
