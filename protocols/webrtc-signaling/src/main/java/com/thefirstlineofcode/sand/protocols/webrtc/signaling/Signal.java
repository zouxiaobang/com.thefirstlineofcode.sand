package com.thefirstlineofcode.sand.protocols.webrtc.signaling;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.Text;
import com.thefirstlineofcode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace="urn:leps:webrtc:signaling", localName="signal")
public class Signal {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:webrtc:signaling", "signal");
	
	public enum ID {
		OFFER,
		ANSWER,
		ICE_CANDIDATE_FOUND
	}
	
	@String2Enum(ID.class)
	private ID id;
	@Text(CDATA=true)
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
	
	@Override
	public String toString() {
		return String.format("Singal[ID: %s, data: %s]", id, data);
	}
}
