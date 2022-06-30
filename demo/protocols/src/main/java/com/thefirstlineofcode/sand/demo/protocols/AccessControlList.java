package com.thefirstlineofcode.sand.demo.protocols;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.Array;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.xmpp.HandyUtils;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace = "http://thefirstlineofcode.com/sand-demo/acl", localName = "query")
public class AccessControlList {
	public static final Protocol PROTOCOL = new Protocol("http://thefirstlineofcode.com/sand-demo/acl", "query");
	public enum Role {
		OWNER,
		VIEWER,
		CONTROLLER
	}
	
	private String deviceId;
	
	@Array(AccessControlEntry.class)
	private List<AccessControlEntry> entries;
	
	public AccessControlList() {
		entries = new ArrayList<AccessControlEntry>();
	}

	public List<AccessControlEntry> getEntries() {
		return entries;
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setEntries(List<AccessControlEntry> entries) {
		this.entries = entries;
	}
	
	public boolean contains(AccessControlEntry entry) {
		if (entry.getUser() == null)
			throw new RuntimeException("Null user.");
		
		if (entry.getDeviceId() == null)
			throw new RuntimeException("Null device ID.");
			
		if (entries == null || entries.isEmpty())
			return false;
		
		for (AccessControlEntry anEntry : entries) {
			if (anEntry.getDeviceId().equals(entry.getDeviceId()) &&
					HandyUtils.equalsEvenNull(anEntry.getUser(), entry.getUser()))
				return true;
		}
		
		return false;
	}
	
	public void add(AccessControlEntry entry) {
		if (contains(entry))
			throw new RuntimeException("Entry has existed.");
		
		entries.add(entry);
	}

	public boolean update(AccessControlEntry entry) {
		for (AccessControlEntry anEntry : entries) {
			if (anEntry.getDeviceId().equals(entry.getDeviceId()) &&
					anEntry.getUser().equals(entry.getUser()))
				anEntry.setRole(entry.getRole());
			
			return true;
		}
		
		throw new RuntimeException(String.format("Entry[%s, %s] doesn't exist.", entry.getDeviceId(), entry.getUser()));
	}
}
