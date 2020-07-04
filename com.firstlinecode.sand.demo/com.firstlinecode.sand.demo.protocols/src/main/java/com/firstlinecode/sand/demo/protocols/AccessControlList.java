package com.firstlinecode.sand.demo.protocols;

import java.util.List;

import com.firstlinecode.basalt.oxm.convention.annotations.Array;
import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace = "http://firstlinecode.com/sand-demo/acl", localName = "query")
public class AccessControlList {
	public enum Role {
		OWNER,
		VIEWER,
		CONTROLLER
	}
	
	@Array(AccessControlEntry.class)
	private List<AccessControlEntry> entries;

	public List<AccessControlEntry> getEntries() {
		return entries;
	}
	
	public void setEntries(List<AccessControlEntry> entries) {
		this.entries = entries;
	}
}
