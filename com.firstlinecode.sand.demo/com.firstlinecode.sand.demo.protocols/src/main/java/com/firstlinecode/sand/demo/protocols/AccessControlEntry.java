package com.firstlinecode.sand.demo.protocols;

import com.firstlinecode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.firstlinecode.basalt.oxm.convention.validation.annotations.NotNull;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;

public class AccessControlEntry {
	@NotNull
	private String deviceId;
	private String parent;
	private String user;
	@NotNull
	@String2Enum(Role.class)
	private Role role;
	
	public AccessControlEntry() {}
	
	public AccessControlEntry(String deviceId, String user, Role role) {
		this(deviceId, user, null, role);
	}
	
	public AccessControlEntry(String deviceId, String user, String parent , Role role) {
		this.deviceId = deviceId;
		this.user = user;
		this.parent = parent;
		this.role = role;
	}
	
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
	
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
}
