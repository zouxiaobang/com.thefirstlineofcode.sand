package com.thefirstlineofcode.sand.demo.server.lite.acl;

import java.util.List;

import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;

public interface AccessControlEntryMapper {
	void insert(AccessControlEntry ace);
	void remove(String user, String deviceId);
	void updateRole(String user, String deviceId, Role role);
	List<AccessControlEntry> selectByUser(String user);
	List<AccessControlEntry> selectByDeviceId(String deviceId);
	String selectOwnerByDeviceId(String deviceId);
	Role selectRoleByUserAndDeviceId(String user, String deviceId);
}
