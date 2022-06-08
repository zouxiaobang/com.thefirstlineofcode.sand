package com.thefirstlineofcode.sand.protocols.things.simple.webcam;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:simple-webcam", localName="open-live-streaming")
public class OpenLiveStreaming {
	public static Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:simple-webcam", "open-live-streaming");
}
