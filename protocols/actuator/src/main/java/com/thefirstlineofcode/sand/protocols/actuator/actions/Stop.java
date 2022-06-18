package com.thefirstlineofcode.sand.protocols.actuator.actions;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:thing", localName="stop")
public class Stop {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:thing", "stop");
}
