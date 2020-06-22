package com.firstlinecode.sand.demo.protocols;

import java.util.List;

import com.firstlinecode.basalt.oxm.convention.annotations.Array;
import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace = "http://firstlinecode.com/sand-demo/acl", localName = "query")
public class AccessControlList {
	@Array(type = AccessControl.class, elementName = "access-control")
	private List<AccessControl> accessControls;

	public List<AccessControl> getAccessControls() {
		return accessControls;
	}
	
	public void setAccessControls(List<AccessControl> accessControls) {
		this.accessControls = accessControls;
	}
}
