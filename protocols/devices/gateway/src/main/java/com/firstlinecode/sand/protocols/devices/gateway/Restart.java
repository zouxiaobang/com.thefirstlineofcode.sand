package com.firstlinecode.sand.protocols.devices.gateway;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.oxm.convention.conversion.annotations.String2DateTime;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.datetime.DateTime;

@ProtocolObject(namespace="urn:leps:iot:actuator:geteway", localName="restart")
public class Restart {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:geteway", "restart");
	
	@String2DateTime
	private DateTime time;

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}
}
