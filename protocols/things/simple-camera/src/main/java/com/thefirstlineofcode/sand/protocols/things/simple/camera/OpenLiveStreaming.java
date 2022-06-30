package com.thefirstlineofcode.sand.protocols.things.simple.camera;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:simple-camera", localName="open-live-streaming")
public class OpenLiveStreaming {
	public static Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:simple-camera", "open-live-streaming");
}
