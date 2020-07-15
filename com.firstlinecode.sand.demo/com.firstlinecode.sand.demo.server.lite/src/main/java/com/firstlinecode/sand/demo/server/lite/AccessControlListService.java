package com.firstlinecode.sand.demo.server.lite;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stream.error.Conflict;
import com.firstlinecode.basalt.protocol.datetime.DateTime;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;
import com.firstlinecode.sand.demo.server.IAccessControlListService;

@Component
@Transactional
public class AccessControlListService implements IAccessControlListService {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void add(AccessControlEntry entry) {
		if (getAccessControlEntryMapper().selectCountByUserAndDevice(entry.getUser(), entry.getDevice()) != 0)
			throw new ProtocolException(new Conflict(String.format("Access control entry which's user is '%s' and device ID is '%s' has already existed.",
					entry.getUser(), entry.getDevice())));
		
		getAccessControlEntryMapper().insert(entry);
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
	public AccessControlList getByUser(String user, DateTime lastModifiedTime) {
		List<AccessControlEntry> entries = getAccessControlEntryMapper().selectByUserAndLastModifiedTime(user, lastModifiedTime.getJavaDate());
		if (entries == null)
			return null;
		
		AccessControlList acl = new AccessControlList();
		acl.setEntries(entries);
		acl.setLastModifiedTime(getLastModifiedTime(entries, lastModifiedTime));
		
		return acl;
	}

	private DateTime getLastModifiedTime(List<AccessControlEntry> entries, DateTime lastModifiedTime) {
		if (entries == null || entries.isEmpty())
			return lastModifiedTime;
		
		Date lastModifiedTimeJavaDate = lastModifiedTime.getJavaDate();
		for (AccessControlEntry entry : entries) {
			D_AccessControlEntry entryImpl = (D_AccessControlEntry)entry;
			if (entryImpl.getLastModifiedTime().after(lastModifiedTimeJavaDate));
				lastModifiedTimeJavaDate = entryImpl.getLastModifiedTime();
		}
		
		return new DateTime(lastModifiedTimeJavaDate);
	}

	@Override
	public AccessControlList getByOwnerAndDevice(String owner, String deviceId, DateTime lastModifiedTime) {
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
		return getAccessControlEntryMapper().selectRoleByUserAndDevice(user, deviceId);
	}

	@Override
	public boolean isOwner(String user, String deviceId) {
		return Role.OWNER == getRole(user, deviceId);
	}
	
	private AccessControlEntryMapper getAccessControlEntryMapper() {
		return sqlSession.getMapper(AccessControlEntryMapper.class);
	}

	@Override
	public AccessControlList getByUser(String user) {
		return getByUser(null);
	}

	@Override
	public AccessControlList getByOwnerAndDevice(String owner, String deviceId) {
		return getByOwnerAndDevice(deviceId, null);
	}

	@Override
	public AccessControlList getByUserAndDevice(String user, String deviceId) {
		return getByUserAndDevice(user, deviceId, null);
	}
}
