package com.thefirstlineofcode.sand.server.devices;

import java.util.Date;

import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class DeviceRegistered {
	public String deviceId;
	public DeviceIdentity deviceIdentity;
	public String authorizer;
	public Date registrationTime;
	
	public DeviceRegistered(String deviceId, DeviceIdentity deviceIdentity,
			String authorizer, Date registrationTime) {
		this.deviceId = deviceId;
		this.deviceIdentity = deviceIdentity;
		this.authorizer = authorizer;
		this.registrationTime = registrationTime;
	}
}
