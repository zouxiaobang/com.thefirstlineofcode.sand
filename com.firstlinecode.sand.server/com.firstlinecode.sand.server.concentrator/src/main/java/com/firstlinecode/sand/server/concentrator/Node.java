package com.firstlinecode.sand.server.concentrator;

public class Node {
	private String concentrator;
	private String node;
	private String lanId;
	private String communicationNet;
	private String address;
	
	public String getConcentrator() {
		return concentrator;
	}
	
	public void setConcentrator(String concentrator) {
		this.concentrator = concentrator;
	}
	
	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
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
