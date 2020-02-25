package com.firstlinecode.sand.protocols.lora.dac;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="urn:leps:iot:dac", localName="introduction")
public class Introduction {
	private String deviceId;
	private long address;
	private int frequencyBand;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public long getAddress() {
		return address;
	}
	
	public void setAddress(long address) {
		this.address = address;
	}
	
	public int getFrequencyBand() {
		return frequencyBand;
	}
	
	public void setFrequencyBand(int frequencyBand) {
		this.frequencyBand = frequencyBand;
	}
	
}
