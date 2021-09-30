package com.firstlinecode.sand.protocols.actuator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator", localName="execute")
public class Execute {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator", "execute");
	
	@NotNull
	private Object action;
	
	public Execute() {}
	
	public Execute(Object action) {
		this.action = action;
	}
	
	public Object getAction() {
		return action;
	}
	
	public void setAction(Object action) {
		this.action = action;
	}
	
}
