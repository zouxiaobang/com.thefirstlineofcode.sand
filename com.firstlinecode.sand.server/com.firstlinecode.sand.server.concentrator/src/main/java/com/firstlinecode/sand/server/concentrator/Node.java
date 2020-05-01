package com.firstlinecode.sand.server.concentrator;

public class Node {
	private String deviceId;
	private String mode;
	private String lanId;
	private String communicationNet;
	private String address;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String node) {
		this.deviceId = node;
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getLanId() {
		return lanId;
	}

	public void setLanId(String lanId) {
		this.lanId = lanId;
	}

	public String getCommunicationNet() {
		return communicationNet;
	}
	
	public void setCommunicationNet(String communicationNet) {
		this.communicationNet = communicationNet;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
}
