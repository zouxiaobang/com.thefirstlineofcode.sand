package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.sand.protocols.core.CommunicationNet;

public class Node {
	private String parent;
	private String deviceId;
	private String lanId;
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
	
	public String getLanId() {
		return lanId;
	}

	public void setLanId(String lanId) {
		this.lanId = lanId;
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
