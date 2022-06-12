package com.thefirstlineofcode.sand.demo.protocols;

import com.thefirstlineofcode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;

public class AuthorizedDevice {
	private String deviceId;
	private String deviceLocation;
	
	@String2Enum(Role.class)
	private Role role;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceLocation() {
		return deviceLocation;
	}
	
	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
}
