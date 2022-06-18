package com.thefirstlineofcode.sand.protocols.actuator.actions;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.BooleanOnly;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:thing", localName="shutdown-system")
public class ShutdownSystem {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:thing", "shutdown-system");
	
	@BooleanOnly
	private boolean restart;
	
	public ShutdownSystem() {
		this(false);
	}
	
	public ShutdownSystem(boolean restart) {
		this.restart = restart;
	}

	public boolean isRestart() {
		return restart;
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}
}
