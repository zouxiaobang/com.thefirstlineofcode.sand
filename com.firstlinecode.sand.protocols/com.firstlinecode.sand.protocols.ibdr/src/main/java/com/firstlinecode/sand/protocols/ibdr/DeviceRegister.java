package com.firstlinecode.sand.protocols.ibdr;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.protocol.oxm.convention.conversion.annotations.String2JabberId;

@ProtocolObject(namespace="http://firstlinecode.com/protocol/device-register", localName="query")
public class DeviceRegister {
	public static final Protocol PROTOCOL = new Protocol("http://firstlinecode.com/protocol/device-register", "query");
	
	private String name;
	@String2JabberId
	private JabberId jid;

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setJid(JabberId jid) {
		this.jid = jid;
	}
	
	public JabberId getJid() {
		return jid;
	}
}
