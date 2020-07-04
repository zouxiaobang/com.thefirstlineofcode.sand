package com.firstlinecode.sand.demo.server;

import java.util.List;

import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;

public interface IAccessControlListService {
	void add(AccessControlEntry entry);
	void change(AccessControlEntry entry);
	void remove(AccessControlEntry entry);
	List<AccessControlList> getByUser(String user);
	AccessControlList getByDevice(String deviceId);
	Role getRole(String user, String deviceId);
	void isOwner(String user, String deviceId);
}
