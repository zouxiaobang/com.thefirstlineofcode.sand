package com.firstlinecode.sand.protocols.concentrator;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:concentrator", localName="node-creation-confirmation")
public class NodeCreationConfirmation {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:concentrator", "node-creation-confirmation");
	
	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
