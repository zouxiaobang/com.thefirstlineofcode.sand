package com.thefirstlineofcode.sand.protocols.things.simple.camera;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:simple-camera", localName="take-video")
public class TakeVideo {
	public static Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:simple-camera", "take-video");
}
