package com.thefirstlineofcode.sand.server.concentrator;

import com.thefirstlinelinecode.sand.protocols.concentrator.NodeCreated;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;

public class ConfirmedEvent implements IEvent {
	private String requestId;
	private NodeCreated nodeCreated;
	
	public ConfirmedEvent(String requestId, NodeCreated nodeCreated) {
		this.requestId = requestId;
		this.nodeCreated = nodeCreated;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public NodeCreated getNodeCreated() {
		return nodeCreated;
	}
	
	public void setNodeCreated(NodeCreated nodeCreated) {
		this.nodeCreated = nodeCreated;
	}
	
	@Override
	public Object clone() {
		return new ConfirmedEvent(requestId, nodeCreated);
	}
	
}
