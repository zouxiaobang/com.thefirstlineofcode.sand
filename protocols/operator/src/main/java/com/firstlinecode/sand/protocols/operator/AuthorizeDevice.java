package com.firstlinecode.sand.protocols.operator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace = "urn:leps:iot:operator", localName = "auth-device")
public class AuthorizeDevice {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:operator", "auth-device");
	
	@NotNull
	private String deviceId;
	private boolean canceled;
	
	public AuthorizeDevice() {}
	
	public AuthorizeDevice(String deviceId) {
		this(deviceId, false);
	}
	
	public AuthorizeDevice(String deviceId, boolean canceled) {
		this.deviceId = deviceId;
		this.canceled = canceled;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
}
