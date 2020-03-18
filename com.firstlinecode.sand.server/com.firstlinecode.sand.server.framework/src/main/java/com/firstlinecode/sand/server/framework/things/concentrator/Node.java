package com.firstlinecode.sand.server.framework.things.concentrator;

import com.firstlinecode.sand.protocols.core.CommunicationNet;

public class Node {
	private String parent;
	private String deviceId;
	private CommunicationNet type;
	private String address;
	
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public CommunicationNet getType() {
		return type;
	}
	
	public void setType(CommunicationNet type) {
		this.type = type;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
}
