package com.firstlinecode.sand.protocols.emulators.light;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="urn:leps:iot:actuator:light", localName="flash")
public class Flash {
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
