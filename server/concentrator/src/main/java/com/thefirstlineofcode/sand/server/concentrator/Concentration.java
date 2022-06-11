package com.thefirstlineofcode.sand.server.concentrator;

import java.util.Date;

public class Concentration {
	private String concentratorDeviceName;
	private String nodeDeviceId;
	private String lanId;
	private String communicationNet;
	private String address;
	private Date creationTime;
	
	public String getConcentratorDeviceName() {
		return concentratorDeviceName;
	}
	
	public void setConcentratorDeviceName(String concentratorDeviceName) {
		this.concentratorDeviceName = concentratorDeviceName;
	}
	
	public String getNodeDeviceId() {
		return nodeDeviceId;
	}
	
	public void setNodeDeviceId(String nodeDeviceId) {
		this.nodeDeviceId = nodeDeviceId;
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
	
	public Date getCreationTime() {
		return creationTime;
	}
	
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
}
