package com.thefirstlineofcode.sand.demo.protocols;

import com.thefirstlineofcode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.protocol.HandyUtils;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;

public class AccessControlEntry {
	public static final String USER_SELF = null;
	
	@NotNull
	private String user;
	private String device;
	private String parent;
	@NotNull
	@String2Enum(Role.class)
	private Role role;
	private Boolean remove;
	
	public AccessControlEntry() {}
	
	public AccessControlEntry(String user, String device) {
		this(user, device, null, null);
	}
	
	public AccessControlEntry(String user, String device, Role role) {
		this(user, device, null, role);
	}
	
	public AccessControlEntry(String user, String device, String parent , Role role) {
		this.user = user;
		this.device = device;
		this.parent = parent;
		this.role = role;
		this.remove = null;
	}
	
	public String getDevice() {
		return device;
	}
	
	public void setDevice(String device) {
		this.device = device;
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
		return (remove != null && remove) ? remove : null;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		
		if (user != null)
			hash += 31 * hash + user.hashCode();
		
		if (device != null)
			hash += 31 * hash + device.hashCode();
		
		if (parent != null)
			hash += 31 * hash + parent.hashCode();
		
		if (role != null)
			hash += 31 * hash + role.hashCode();
		
		if (remove != null)
			hash += 31 * hash + remove.hashCode();
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AccessControlEntry) {
			AccessControlEntry other = (AccessControlEntry)obj;
			if (!this.user.equals(other.user))
				return false;
			
			if (!HandyUtils.equalsEvenNull(this.device, other.device))
				return false;
			
			if (!HandyUtils.equalsEvenNull(this.parent, other.parent))
				return false;
			
			if (!this.role.equals(other.role))
				return false;
			
			return true;
		} else {
			return super.equals(obj);
		}
	}
	
}
