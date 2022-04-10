package com.thefirstlineofcode.sand.protocols.devices.light;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:light", localName="flash")
public class Flash {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:light", "flash");
	
	public static final String ERROR_CODE_NOT_REMOTE_CONTROL_STATE = "01";
	
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
	
	@Override
	public String toString() {
		return String.format("Flash[repeat=%d]", repeat);
	}
}
