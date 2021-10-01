package com.thefirstlineofcode.sand.emulators.lora.gateway.things;

import java.io.Serializable;

public class DeviceIdentityInfo implements Serializable {
	private static final long serialVersionUID = -5028042430191491446L;
	
	public String deviceName;
	public String credentials;
	
	public DeviceIdentityInfo(String deviceName, String credentials) {
		this.deviceName = deviceName;
		this.credentials = credentials;
	}
}
