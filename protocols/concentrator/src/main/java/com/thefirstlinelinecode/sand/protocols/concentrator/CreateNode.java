package com.thefirstlinelinecode.sand.protocols.concentrator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:concentrator", localName="create-node")
public class CreateNode {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:concentrator", "create-node");
	
	@NotNull
	private String deviceId;
	@NotNull
	private String lanId;
	@NotNull
	private String communicationNet;
	@NotNull
	private String address;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getLanId() {
		return lanId;
	}
	
	public void setLanId(String lanId) {
		this.lanId = lanId;
	}

	public String getCommunicationNet() {
		return communicationNet;
	}

	public void setCommunicationNet(String communicationNet) {
		this.communicationNet = communicationNet;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
