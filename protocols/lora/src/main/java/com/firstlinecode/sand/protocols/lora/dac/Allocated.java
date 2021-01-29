package com.firstlinecode.sand.protocols.lora.dac;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:dac", localName="allocated")
public class Allocated {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:dac", "allocated");

	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
