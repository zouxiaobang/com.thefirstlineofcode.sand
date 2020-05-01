package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.sand.protocols.concentrator.NodeCreated;

public class Confirmed {
	private String requestId;
	private NodeCreated nodeCreated;
	
	public Confirmed(String requestId, NodeCreated nodeCreated) {
		this.requestId = requestId;
		this.nodeCreated = nodeCreated;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public NodeCreated getNodeCreated() {
		return nodeCreated;
	}
}
