package com.firstlinecode.sand.protocols.ibdr;

import com.firstlinecode.basalt.protocol.core.JabberId;

public class DeviceIdentity {
	private JabberId jid;
	private String credentials;
	
	public DeviceIdentity() {}
	
	public DeviceIdentity(JabberId jid, String credentials) {
		this.jid = jid;
		this.credentials = credentials;
	}

	public void setJid(JabberId jid) {
		this.jid = jid;
	}
	
	public JabberId getJid() {
		return jid;
	}
	
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getCredentials() {
		return credentials;
	}
	
}
