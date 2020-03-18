package com.firstlinecode.sand.client.concentrator;

import com.firstlinecode.sand.protocols.concentrator.NodeAddress;

public class Node {
	private String deviceId;
	private NodeAddress<?> address;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public NodeAddress<?> getAddress() {
		return address;
	}
	
	public void setAddress(NodeAddress<?> address) {
		this.address = address;
	}
}
