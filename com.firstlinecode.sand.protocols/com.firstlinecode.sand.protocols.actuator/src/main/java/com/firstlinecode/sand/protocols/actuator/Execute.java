package com.firstlinecode.sand.protocols.actuator;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator", localName="execute")
public class Execute {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator", "execute");
	
	private String actionName;
	private Object action;
	
	public Execute() {}
	
	public Execute(String actionName, Object action) {
		this.actionName = actionName;
		this.action = action;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	public Object getAction() {
		return action;
	}
	
	public void setAction(Object action) {
		this.action = action;
	}
	
}
