package com.firstlinecode.sand.client.actuator;

public class ExecutionException extends Exception {
	private static final long serialVersionUID = 7996516370515700261L;
	
	public enum Reason {
		UNSUPPORTED_ACTION_TYPE,
		INTERNAL_ERROR
	}
	
	private Reason reason;
	private Throwable cause;

	public ExecutionException(Reason reason) {
		this.reason = reason;
	}
	
	public ExecutionException(Throwable cause) {
		this.reason = Reason.INTERNAL_ERROR;
		this.cause = cause;
	}
	
	public ExecutionException.Reason getReason() {
		return reason;
	}
	
	public Throwable getCause() {
		return cause;
	}
}
