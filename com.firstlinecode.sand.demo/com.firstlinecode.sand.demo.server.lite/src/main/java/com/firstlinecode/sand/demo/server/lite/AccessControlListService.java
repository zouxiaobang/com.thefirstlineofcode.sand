package com.firstlinecode.sand.demo.server.lite;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;
import com.firstlinecode.sand.demo.server.IAccessControlListService;

@Component
@Transactional
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
	public List<AccessControlList> getByUser(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getByDevice(String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getRole(String user, String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void isOwner(String user, String deviceId) {
		// TODO Auto-generated method stub

	}

}
