package com.firstlinecode.sand.demo.protocols;

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
	
	@String2DateTime
	private DateTime lastModifiedTime;
	@Array(AccessControlEntry.class)
	private List<AccessControlEntry> entries;
	
	public AccessControlList() {}
	
	public AccessControlList(DateTime lastModDateTime) {
		this.lastModifiedTime = lastModDateTime;
	}

	public DateTime getLastModifiedTime() {
		return lastModifiedTime;
	}
	
	public void setLastModifiedTime(DateTime lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public List<AccessControlEntry> getEntries() {
		return entries;
	}
	
	public void setEntries(List<AccessControlEntry> entries) {
		this.entries = entries;
	}
}
