package com.thefirstlineofcode.sand.protocols.actuator;

public class LanActionException extends Exception {
	private static final long serialVersionUID = 4957236030809756512L;
	
	private String errorCode;
	
	public LanActionException(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
