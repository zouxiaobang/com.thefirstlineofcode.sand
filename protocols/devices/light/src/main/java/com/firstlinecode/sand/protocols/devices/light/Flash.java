package com.firstlinecode.sand.protocols.devices.light;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

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
		if (repeat < 1)
			throw new IllegalArgumentException("Attribute repeat must be a positive integer.");
		
		this.repeat = repeat;
	}
	
}
