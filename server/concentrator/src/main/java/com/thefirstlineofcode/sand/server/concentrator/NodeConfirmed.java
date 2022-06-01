package com.thefirstlineofcode.sand.server.concentrator;

import java.util.Date;

public class NodeConfirmed {
	private String requestId;
	private String concentratorDeviceName;
	private String nodeDeviceId;
	private String lanId;
	private String model;
	private String confirmer;
	private Date creationTime;
	private Date confirmedTime;
	
	public NodeConfirmed(String requestId, String concentratorDeviceName, String nodeDeviceId, String lanId,
			String model, String confirmer, Date creationTime, Date confirmedTime) {
		this.requestId = requestId;
		this.concentratorDeviceName = concentratorDeviceName;
		this.nodeDeviceId = nodeDeviceId;
		this.lanId = lanId;
		this.model = model;
		this.confirmer = confirmer;
		this.creationTime = creationTime;
		this.confirmedTime = confirmedTime;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public String getConcentratorDeviceName() {
		return concentratorDeviceName;
	}
	
	public String getNodeDeviceId() {
		return nodeDeviceId;
	}
	
	public String getLanId() {
		return lanId;
	}
	
	public String getModel() {
		return model;
	}
	
	public String getConfirmer() {
		return confirmer;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}
	
	public Date getConfirmedTime() {
		return confirmedTime;
	}	
	
}
