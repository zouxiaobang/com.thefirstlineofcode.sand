package com.firstlinecode.sand.protocols.lora.dac;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="urn:leps:iot:dac", localName="confirmation")
public class Confirmation {
	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
