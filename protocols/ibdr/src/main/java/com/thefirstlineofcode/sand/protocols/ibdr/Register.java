package com.thefirstlineofcode.sand.protocols.ibdr;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.stream.Feature;

@ProtocolObject(namespace="urn:leps:iot:ibdr", localName="register")
public class Register implements Feature {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:ibdr", "register");
}
