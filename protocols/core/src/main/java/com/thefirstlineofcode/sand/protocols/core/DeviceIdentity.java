package com.thefirstlineofcode.sand.protocols.core;

import java.io.Serializable;

public class DeviceIdentity implements Serializable {
	private static final long serialVersionUID = 2975514130104649088L;
	
	public static final String DEFAULT_RESOURCE_NAME = "00";
	
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
