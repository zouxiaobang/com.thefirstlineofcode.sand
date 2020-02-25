package com.firstlinecode.sand.protocols.concentrator;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="urn:leps:iot:concentrator", localName="create-node")
public class CreateNode<T> {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:concentrator", "create-node");
	
	private String deviceId;
	private String lanId;
	private T address;
	
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

	public T getAddress() {
		return address;
	}

	public void setAddress(T address) {
		this.address = address;
	}
	
}
