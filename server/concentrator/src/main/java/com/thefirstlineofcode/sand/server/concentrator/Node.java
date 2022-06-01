package com.thefirstlineofcode.sand.server.concentrator;

public class Node {
	private String deviceId;
	private String lanId;
	private String model;
	private String communicationNet;
	private String address;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String node) {
		this.deviceId = node;
	}
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
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
