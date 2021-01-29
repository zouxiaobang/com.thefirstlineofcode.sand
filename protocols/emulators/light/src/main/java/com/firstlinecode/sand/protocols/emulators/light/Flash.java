package com.firstlinecode.sand.protocols.emulators.light;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:light", localName="flash")
public class Flash {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:light", "flash");
	
	private int repeat;
	
	public Flash() {
		repeat = 1;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	
}
