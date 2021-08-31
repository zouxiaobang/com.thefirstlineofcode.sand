package com.firstlinecode.sand.server.lite.concentrator;

import java.util.Date;

import com.firstlinecode.granite.framework.core.adf.data.IIdProvider;

public class Concentration implements IIdProvider<String> {
	private String id;
	private String concentrator;
	private String node;
	private String lanId;
	private String communicationNet;
	private String address;
	private Date confirmationTime;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
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
	
	public Date getConfirmationTime() {
		return confirmationTime;
	}
	
	public void setConfirmationTime(Date confirmationTime) {
		this.confirmationTime = confirmationTime;
	}
}
