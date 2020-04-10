package com.firstlinecode.sand.server.device;

import java.util.Date;

public class Device {
	private String deviceId;
	private String mode;
	private String softwareVersion;
	private Date registrationTime;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
	
}
