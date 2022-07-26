package com.thefirstlineofcode.sand.protocols.webrtc.signaling;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace="urn:leps:webrtc:signaling", localName="signal")
public class Signal {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:webrtc:signaling", "signal");
	
	public enum ID {
		OFFER,
		ANSWER
	}
	
	@String2Enum(ID.class)
	private ID id;
	private String data;
	
	public Signal() {}
	
	public Signal(ID id, String data) {
		this.id = id;
		this.data = data;
	}
	
	public ID getId() {
		return id;
	}
	
	public void setId(ID id) {
		this.id = id;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
}
