package com.thefirstlineofcode.sand.protocols.actuator;

public class LanActionException extends Exception {
	private static final long serialVersionUID = 4957236030809756512L;
	
	private LanActionError error;
	
	public LanActionException(LanActionError error) {
		this.error = error;
	}

	public LanActionError getError() {
		return error;
	}

}
