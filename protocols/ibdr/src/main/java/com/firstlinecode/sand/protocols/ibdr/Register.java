package com.firstlinecode.sand.protocols.ibdr;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.core.stream.Feature;
import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="urn:leps:iot:ibdr", localName="register")
public class Register implements Feature {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:ibdr", "register");
}
