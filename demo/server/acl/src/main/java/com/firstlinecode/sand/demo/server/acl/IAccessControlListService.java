package com.firstlinecode.sand.demo.server.acl;

import com.firstlinecode.basalt.protocol.datetime.DateTime;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;

public interface IAccessControlListService {
	void add(AccessControlEntry entry);
	void change(AccessControlEntry entry);
	void remove(AccessControlEntry entry);
	AccessControlList getByUser(String user);
	AccessControlList getByUser(String user, DateTime lastModifiedTime);
	AccessControlList getByOwnerAndDevice(String owner, String deviceId);
	AccessControlList getByOwnerAndDevice(String owner, String deviceId, DateTime lastModifiedTime);
	AccessControlList getByUserAndDevice(String user, String deviceId);
	AccessControlList getByUserAndDevice(String user, String deviceId, DateTime lastModifiedTime);
	Role getRole(String user, String deviceId);
	boolean isOwner(String user, String deviceId);
}
