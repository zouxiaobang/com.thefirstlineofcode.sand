package com.firstlinecode.sand.protocols.operator;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace = "urn:leps:iot:operator", localName = "confirm-concentration")
public class ConfirmConcentration {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:operator", "confirm-concentration");
}
