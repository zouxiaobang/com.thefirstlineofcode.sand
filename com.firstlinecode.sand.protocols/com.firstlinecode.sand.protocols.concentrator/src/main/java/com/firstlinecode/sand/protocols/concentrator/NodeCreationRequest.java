package com.firstlinecode.sand.protocols.concentrator;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:concentrator", localName="node-creation-request")
public class NodeCreationRequest<T> {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:concentrator", "node-creation-request");
	
	private String deviceId;
	private String lanId;
	private NodeAddress<T> address;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getLanId() {
		return lanId;
	}
	
	public void setLanId(String lanId) {
		this.lanId = lanId;
	}

	public NodeAddress<T> getAddress() {
		return address;
	}

	public void setAddress(NodeAddress<T> address) {
		this.address = address;
	}
	
}
