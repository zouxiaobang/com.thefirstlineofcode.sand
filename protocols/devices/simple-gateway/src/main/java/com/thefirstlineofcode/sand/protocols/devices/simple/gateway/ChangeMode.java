package com.thefirstlineofcode.sand.protocols.devices.simple.gateway;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.conversion.annotations.String2Enum;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:geteway", localName="change-mode")
public class ChangeMode {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:geteway", "change-mode");
	
	public enum Mode {
		ADDRESS_CONFIGURATION {
			@Override
			public String toString() {
				return "A";
			}
		},
		WORKING {
			@Override
			public String toString() {
				return "W";
			}
		}
	}
	
	@String2Enum(Mode.class)
	private Mode mode;
	
	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
}
