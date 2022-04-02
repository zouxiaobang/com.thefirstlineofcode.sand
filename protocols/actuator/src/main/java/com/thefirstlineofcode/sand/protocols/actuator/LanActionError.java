package com.thefirstlineofcode.sand.protocols.actuator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator", localName="lan-action-error")
public class LanActionError {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator", "lan-action-error");
	
	@NotNull
	private String errorCode;
	private String message;
	
	public LanActionError() {}
	
	public LanActionError(String errorCode) {
		this(errorCode, null);
	}
	
	public LanActionError(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
