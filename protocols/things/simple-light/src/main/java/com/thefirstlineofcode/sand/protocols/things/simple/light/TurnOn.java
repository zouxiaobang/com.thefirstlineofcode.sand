package com.thefirstlineofcode.sand.protocols.things.simple.light;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:simple-light", localName="turn-on")
public class TurnOn {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:simple-light", "turn-on");
}
