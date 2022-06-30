package com.thefirstlineofcode.sand.protocols.operator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace = "urn:leps:iot:operator", localName = "confirm-concentration")
public class ConfirmConcentration {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:operator", "confirm-concentration");
	
	private String concentratorDeviceId;
	private String nodeDeviceId;
	
	public String getConcentratorDeviceId() {
		return concentratorDeviceId;
	}
	
	public void setConcentratorDeviceId(String concentratorDeviceId) {
		this.concentratorDeviceId = concentratorDeviceId;
	}
	
	public String getNodeDeviceId() {
		return nodeDeviceId;
	}
	
	public void setNodeDeviceId(String nodeDeviceId) {
		this.nodeDeviceId = nodeDeviceId;
	}
}
