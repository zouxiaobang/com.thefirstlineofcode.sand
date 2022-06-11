package com.thefirstlineofcode.sand.protocols.things.simple.camera;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:simple-camera", localName="take-photo")
public class TakePhoto {
	public static Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:simple-camera", "take-photo");
}
