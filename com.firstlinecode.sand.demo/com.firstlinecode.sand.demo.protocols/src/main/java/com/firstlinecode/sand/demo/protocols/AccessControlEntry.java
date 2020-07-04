package com.firstlinecode.sand.demo.protocols;

import com.firstlinecode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;

public class AccessControlEntry {
	private String deviceId;
	private String user;
	@String2Enum(Role.class)
	private Role role;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
}
