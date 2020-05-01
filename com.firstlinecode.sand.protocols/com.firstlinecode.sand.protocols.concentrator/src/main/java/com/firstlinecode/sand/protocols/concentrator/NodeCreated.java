package com.firstlinecode.sand.protocols.concentrator;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.oxm.convention.validation.annotations.NotNull;
import com.firstlinecode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:concentrator", localName="node-created")
public class NodeCreated {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:concentrator", "node-created");
	
	@NotNull
	private String concentrator;
	@NotNull
	private String node;
	@NotNull
	private String lanId;
	@NotNull
	private String mode;
	
	public NodeCreated() {}
	
	public NodeCreated(String concentrator, String node, String lanId, String mode) {
		this.concentrator = concentrator;
		this.node = node;
		this.lanId = lanId;
		this.mode = mode;
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
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
}
