package com.firstlinecode.sand.demo.server.lite.acl;

import com.firstlinecode.basalt.protocol.datetime.DateTime;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;
import com.firstlinecode.sand.demo.server.acl.IAccessControlListService;

public class AccessControlListService implements IAccessControlListService {

	@Override
	public void add(AccessControlEntry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void change(AccessControlEntry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(AccessControlEntry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AccessControlList getByUser(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getByUser(String user, DateTime lastModifiedTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getByOwnerAndDevice(String owner, String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getByOwnerAndDevice(String owner, String deviceId, DateTime lastModifiedTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getByUserAndDevice(String user, String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getByUserAndDevice(String user, String deviceId, DateTime lastModifiedTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getRole(String user, String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOwner(String user, String deviceId) {
		// TODO Auto-generated method stub
		return false;
	}

}
