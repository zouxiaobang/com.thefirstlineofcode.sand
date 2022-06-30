package com.thefirstlineofcode.sand.protocols.ibdr;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

@ProtocolObject(namespace="urn:leps:iot:ibdr", localName="query")
public class DeviceRegister {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:ibdr", "query");
	
	private Object register;
	
	public DeviceRegister() {}
	
	public DeviceRegister(Object register) {
		if (!(register instanceof String) && !(register instanceof DeviceIdentity))
			throw new IllegalArgumentException("Register object must be a string or a device identity.");
		
		this.register = register;
	}

	public void setRegister(Object register) {
		this.register = register;
	}
	
	public Object getRegister() {
		return register;
	}
}
