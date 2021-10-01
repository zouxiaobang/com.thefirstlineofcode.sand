package com.thefirstlinelinecode.sand.protocols.concentrator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

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
	private String model;
	
	public NodeCreated() {}
	
	public NodeCreated(String concentrator, String node, String lanId, String model) {
		this.concentrator = concentrator;
		this.node = node;
		this.lanId = lanId;
		this.model = model;
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
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
}
