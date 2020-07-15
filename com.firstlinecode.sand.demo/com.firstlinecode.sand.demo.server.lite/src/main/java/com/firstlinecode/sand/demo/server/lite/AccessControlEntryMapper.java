package com.firstlinecode.sand.demo.server.lite;

import java.util.Date;
import java.util.List;

import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;

public interface AccessControlEntryMapper {
	void insert(AccessControlEntry entry);
	Role selectRoleByUserAndDevice(String user, String deviceId);
	List<AccessControlEntry> selectByUser(String user);
	List<AccessControlEntry> selectByUserAndLastModifiedTime(String user, Date lastModifiedTime);
	List<AccessControlEntry> selectByOwnerAndDevice(String user, String device);
	List<AccessControlEntry> selectByOwnerAndDevice(String user, String device, Date lastModifiedTime);
	int selectCountByUserAndDevice(String user, String device);
	AccessControlEntry selectByUserAndDevice(String user, String device);
	AccessControlEntry selectByUserAndDevice(String user, String device, Date lastModifiedTime);
}
