package com.firstlinecode.sand.emulators.gateway;

import java.io.Serializable;

public class DeviceIdentityInfo implements Serializable {
	private static final long serialVersionUID = -5028042430191491446L;
	
	public String jid;
	public String credentials;
	
	public DeviceIdentityInfo(String jid, String credentials) {
		this.jid = jid;
		this.credentials = credentials;
	}
}
