package com.firstlinecode.sand.demo.protocols;

import java.util.Collections;
import java.util.List;

import com.firstlinecode.basalt.oxm.convention.annotations.Array;
import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.oxm.convention.conversion.annotations.String2DateTime;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.datetime.DateTime;

@ProtocolObject(namespace = "http://firstlinecode.com/sand-demo/acl", localName = "query")
public class AccessControlList {
	public static final Protocol PROTOCOL = new Protocol("http://firstlinecode.com/sand-demo/acl", "query");
	public enum Role {
		OWNER,
		VIEWER,
		CONTROLLER
	}
	
	private String deviceId;
	@String2DateTime
	private DateTime lastModifiedTime;
	@Array(AccessControlEntry.class)
	private List<AccessControlEntry> entries;
	
	public AccessControlList() {}
	
	public AccessControlList(DateTime lastModDateTime) {
		this.lastModifiedTime = lastModDateTime;
	}
	
	public AccessControlList(String deviceId, DateTime lastModDateTime) {
		this.deviceId = deviceId;
		this.lastModifiedTime = lastModDateTime;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public DateTime getLastModifiedTime() {
		return lastModifiedTime;
	}
	
	public void setLastModifiedTime(DateTime lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public List<AccessControlEntry> getEntries() {
		if (entries == null)
			return Collections.emptyList();
		
		return Collections.unmodifiableList(entries);
	}
	
	public void setEntries(List<AccessControlEntry> entries) {
		this.entries = entries;
		
		if (deviceId == null)
			return;
		
		for (AccessControlEntry entry : entries) {
			if (entry.getDeviceId() == null)
				entry.setDeviceId(deviceId);
		}
	}
	
	public boolean contains(AccessControlEntry ace) {
		if (ace.getDeviceId() == null)
			throw new RuntimeException("Null device ID.");
			
		if (entries == null || entries.isEmpty())
			return false;
		
		for (AccessControlEntry entry : entries) {
			if (entry.getDeviceId().equals(ace.getDeviceId()) &&
					entry.getUser().equals(entry.getUser()))
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
