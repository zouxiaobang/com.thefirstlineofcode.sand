package com.thefirstlineofcode.sand.server.concentrator;

import java.util.Date;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;

public class NodeConfirmationEvent implements IEvent  {
	private String requestId;
	private String concentratorDeviceName;
	private String nodeDeviceId;
	private String lanId;
	private String model;
	private String confirmer;
	private Date creationTime;
	
	public NodeConfirmationEvent(String requestId, String concentratorDeviceName, String nodeDeviceId,
			String lanId, String model, String confirmer, Date creationTime) {
		this.requestId = requestId;
		this.concentratorDeviceName = concentratorDeviceName;
		this.nodeDeviceId = nodeDeviceId;
		this.lanId = lanId;
		this.model = model;
		this.confirmer = confirmer;
		this.creationTime = creationTime;
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

	@Override
	public Object clone() {
		return new NodeConfirmationEvent(requestId, concentratorDeviceName, nodeDeviceId, lanId, model, confirmer, creationTime);
	}
}
