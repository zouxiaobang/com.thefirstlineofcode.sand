package com.thefirstlineofcode.sand.protocols.things.simple.webcam;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:simple-webcam", localName="take-photo")
public class TakePhoto {
	public static Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:simple-webcam", "take-photo");
}
