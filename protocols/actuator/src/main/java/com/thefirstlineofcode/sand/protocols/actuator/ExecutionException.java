package com.thefirstlineofcode.sand.protocols.actuator;

public class ExecutionException extends Exception {
	private static final long serialVersionUID = 4957236030809756512L;
	
	private String errorCode;
	
	public ExecutionException(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
