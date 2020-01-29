package com.firstlinecode.sand.protocols.core;

public class DeviceIdentity {
	private String deviceName;
	private String credentials;
	
	public DeviceIdentity() {}
	
	public DeviceIdentity(String deviceName, String credentials) {
		this.deviceName = deviceName;
		this.credentials = credentials;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getCredentials() {
		return credentials;
	}
	
}
