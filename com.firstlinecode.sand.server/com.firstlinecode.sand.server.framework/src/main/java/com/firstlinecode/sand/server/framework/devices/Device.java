package com.firstlinecode.sand.server.framework.devices;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.datetime.Date;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class Device {
	private String deviceId;
	private DeviceIdentity identity;
	private String mode;
	private String softwareVersion;
	private Date registrationTime;
	private JabberId confirmer;
	
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
	
	public JabberId getConfirmer() {
		return confirmer;
	}
	
	public void setConfirmer(JabberId comfirmer) {
		this.confirmer = comfirmer;
	}
}
