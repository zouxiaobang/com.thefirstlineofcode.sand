package com.thefirstlineofcode.sand.demo.protocols;

import com.thefirstlineofcode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.thefirstlineofcode.basalt.xmpp.HandyUtils;
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
	
	@Override
	public int hashCode() {
		int hash = 7;
		
		if (deviceId != null)
			hash += 31 * hash + deviceId.hashCode();
		
		if (deviceLocation != null)
			hash += 31 * hash + deviceLocation.hashCode();
		
		if (role != null)
			hash += 31 * hash + role.hashCode();
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AuthorizedDevice))
			return false;
		
		AuthorizedDevice other = (AuthorizedDevice)obj;
		
		return HandyUtils.equalsEvenNull(this.deviceId, other.deviceId) &&
				HandyUtils.equalsEvenNull(this.deviceLocation, this.deviceLocation) &&
				HandyUtils.equalsEvenNull(this.role, this.role);
	}
}
