package com.firstlinecode.sand.server.device;

import java.util.Date;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class Device {
	private String deviceId;
	private String authorizationId;
	private DeviceIdentity identity;
	private String mode;
	private String softwareVersion;
	private Date registrationTime;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public DeviceIdentity getIdentity() {
		return identity;
	}
	
	public void setIdentity(DeviceIdentity identity) {
		this.identity = identity;
	}

	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	
	public Date getRegistrationTime() {
		return registrationTime;
	}
	
	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(String authorization) {
		this.authorizationId = authorization;
	}
	
}
