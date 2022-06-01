package com.thefirstlinelinecode.sand.protocols.concentrator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:concentrator", localName="node-created")
public class NodeCreated {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:concentrator", "node-created");
	
	@NotNull
	private String concentratorDeviceName;
	@NotNull
	private String nodeDeviceId;
	@NotNull
	private String lanId;
	@NotNull
	private String model;
	
	public NodeCreated() {}
	
	public NodeCreated(String concentratorDeviceName, String nodeDeviceId, String lanId, String model) {
		this.concentratorDeviceName = concentratorDeviceName;
		this.nodeDeviceId = nodeDeviceId;
		this.lanId = lanId;
		this.model = model;
	}
	
	public String getConcentratorDeviceName() {
		return concentratorDeviceName;
	}



	public void setConcentratorDeviceName(String concentratorDeviceName) {
		this.concentratorDeviceName = concentratorDeviceName;
	}
	
	public String getNodeDeviceId() {
		return nodeDeviceId;
	}
	
	public void setNodeDeviceId(String nodeDeviceId) {
		this.nodeDeviceId = nodeDeviceId;
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
