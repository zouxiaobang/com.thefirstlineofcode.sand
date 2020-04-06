package com.firstlinecode.sand.server.concentrator;

import java.util.Date;

public class NodeConfirmationRequest {
	private Node node;
	private String confirmer;
	private Date confirmedTime;
	private Date expiredTime;
	private boolean canceled;
	
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
