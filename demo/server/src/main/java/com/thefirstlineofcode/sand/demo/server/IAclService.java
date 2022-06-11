package com.thefirstlineofcode.sand.demo.server;

import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;

public interface IAclService {
	void add(AccessControlEntry ace);
	void change(AccessControlEntry ace);
	void remove(String user, String deviceId);
	AccessControlList getUserAcl(String user);
	AccessControlList getOwnerAcl(String deviceId);
	Role getRole(String user, String deviceId);
	String getOwner(String deviceId);
	boolean isOwner(String user, String deviceId);
}
