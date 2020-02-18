package com.firstlinecode.sand.protocols.core.lora;

import com.firstlinecode.basalt.protocol.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="http://firstlinecode.com/protocols/address-configuration", localName="confirmation")
public class Confirmation {
	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
