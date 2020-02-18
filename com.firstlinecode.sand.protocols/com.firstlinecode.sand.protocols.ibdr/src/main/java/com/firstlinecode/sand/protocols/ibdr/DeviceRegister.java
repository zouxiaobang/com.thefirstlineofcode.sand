package com.firstlinecode.sand.protocols.ibdr;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

@ProtocolObject(namespace="http://firstlinecode.com/protocols/device-register", localName="query")
public class DeviceRegister {
	public static final Protocol PROTOCOL = new Protocol("http://firstlinecode.com/protocols/device-register", "query");
	
	private Object register;
	
	public DeviceRegister() {}
	
	public DeviceRegister(Object register) {
		if (!(register instanceof String) && !(register instanceof DeviceIdentity))
			throw new IllegalArgumentException("Register object must be String or JabberId.");
		
		this.register = register;
	}

	public void setRegister(Object register) {
		this.register = register;
	}
	
	public Object getRegister() {
		return register;
	}
}
