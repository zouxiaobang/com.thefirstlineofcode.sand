package com.thefirstlineofcode.sand.server.concentrator;

import java.util.Date;

public class NodeConfirmation {
	private String requestId;
	private String concentratorDeviceName;
	private Node node;
	private String confirmer;
	private Date requestedTime;
	private Date confirmedTime;
	private Date expiredTime;
	private boolean canceled;
	
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String getConcentratorDeviceName() {
		return concentratorDeviceName;
	}

	public void setConcentratorDeviceName(String concentratorDeviceName) {
		this.concentratorDeviceName = concentratorDeviceName;
	}

	public Node getNode() {
		return node;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public String getConfirmer() {
		return confirmer;
	}
	
	public void setConfirmer(String confirmer) {
		this.confirmer = confirmer;
	}
	
	public Date getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(Date requestedTime) {
		this.requestedTime = requestedTime;
	}

	public Date getConfirmedTime() {
		return confirmedTime;
	}
	
	public void setConfirmedTime(Date confirmedTime) {
		this.confirmedTime = confirmedTime;
	}
	
	public Date getExpiredTime() {
		return expiredTime;
	}
	
	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
