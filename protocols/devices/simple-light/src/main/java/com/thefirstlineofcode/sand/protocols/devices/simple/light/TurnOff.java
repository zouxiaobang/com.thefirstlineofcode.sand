package com.thefirstlineofcode.sand.protocols.devices.simple.light;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:light", localName="turn-off")
public class TurnOff {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:light", "turn-off");
}
