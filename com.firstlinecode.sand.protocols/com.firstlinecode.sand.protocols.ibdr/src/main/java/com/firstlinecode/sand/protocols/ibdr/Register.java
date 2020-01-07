package com.firstlinecode.sand.protocols.ibdr;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.core.stream.Feature;
import com.firstlinecode.basalt.protocol.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="http://firstlinecode.com/features/device-register", localName="register")
public class Register implements Feature {
	public static final Protocol PROTOCOL = new Protocol("http://firstlinecode.com/features/device-register", "register");
}
