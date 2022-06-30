package com.thefirstlineofcode.sand.demo.protocols;

import java.util.List;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.Array;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace = "http://thefirstlineofcode.com/sand-demo/authorized-devices", localName = "query")
public class AuthorizedDevices {
	public static final Protocol PROTOCOL = new Protocol("http://thefirstlineofcode.com/sand-demo/authorized-devices", "query");
	
	@Array(value=AuthorizedDevice.class, elementName = "device")
	private List<AuthorizedDevice> devices;
	
	public AuthorizedDevices() {}
	
	public AuthorizedDevices(List<AuthorizedDevice> devices) {
		this.devices = devices;
	}

	public List<AuthorizedDevice> getDevices() {
		return devices;
	}

	public void setDevices(List<AuthorizedDevice> devices) {
		this.devices = devices;
	}
}
