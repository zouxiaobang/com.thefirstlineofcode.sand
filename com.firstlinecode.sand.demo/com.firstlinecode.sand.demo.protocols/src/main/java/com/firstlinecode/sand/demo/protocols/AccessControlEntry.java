package com.firstlinecode.sand.demo.protocols;

import com.firstlinecode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.firstlinecode.basalt.oxm.convention.validation.annotations.NotNull;
import com.firstlinecode.basalt.protocol.HandyUtils;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;

public class AccessControlEntry {
	private String deviceId;
	private String parent;
	@NotNull
	private String user;
	@NotNull
	@String2Enum(Role.class)
	private Role role;
	private Boolean remove;
	
	public AccessControlEntry() {}
	
	public AccessControlEntry(String deviceId, String user) {
		this(deviceId, user, null, null);
	}
	
	public AccessControlEntry(String deviceId, String user, Role role) {
		this(deviceId, user, null, role);
	}
	
	public AccessControlEntry(String deviceId, String user, String parent , Role role) {
		this.deviceId = deviceId;
		this.user = user;
		this.parent = parent;
		this.role = role;
		this.remove = null;
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
	
	public Boolean getRemove() {
		if (remove)
			return remove;
		
		return null;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		
		if (deviceId != null)
			hash += 31 * hash + deviceId.hashCode();
		
		if (parent != null)
			hash += 31 * hash + parent.hashCode();
		
		hash += 31 * hash + user.hashCode();
		hash += 31 * hash + role.hashCode();
		
		if (remove != null)
			hash += 31 * hash + remove.hashCode();
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AccessControlEntry) {
			AccessControlEntry other = (AccessControlEntry)obj;
			if (!HandyUtils.equalsEvenNull(this.deviceId, other.deviceId))
				return false;
			
			if (!HandyUtils.equalsEvenNull(this.parent, other.parent))
				return false;
			
			if (!this.user.equals(other.user))
				return false;
			
			if (!this.role.equals(other.role))
				return false;
			
			return true;
		} else {
			return super.equals(obj);
		}
	}
	
}
