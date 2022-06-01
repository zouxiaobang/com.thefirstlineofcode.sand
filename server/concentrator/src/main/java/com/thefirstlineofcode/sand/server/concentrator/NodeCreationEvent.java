package com.thefirstlineofcode.sand.server.concentrator;

import java.util.Date;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;

public class NodeCreationEvent implements IEvent {
	private String concentratorDeviceName;
	private String nodeDeviceId;
	private String lanId;
	private String confirmer;
	private Date creationTime;
	
	public NodeCreationEvent(String concentratorDeviceName, String nodeDeviceId,
			String lanId, String confirmer, Date creationTime) {
		this.concentratorDeviceName = concentratorDeviceName;
		this.nodeDeviceId = nodeDeviceId;
		this.confirmer = confirmer;
		this.lanId = lanId;
		this.creationTime = creationTime;
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
	
	public String getConfirmer() {
		return confirmer;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}
	
	@Override
	public Object clone() {
		return new NodeCreationEvent(concentratorDeviceName, nodeDeviceId, lanId, confirmer, creationTime);
	}
}
