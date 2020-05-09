package com.firstlinecode.sand.client.actuator;

public class ExecutionException extends Exception {
	private static final long serialVersionUID = 7996516370515700261L;
	
	public enum Reason {
		UNSUPPORTED_ACTION_TYPE,
		FAILED_TO_CREATE_INSTANCE,
		FAILED_TO_EXECUTE,
		NOT_A_CONCENTRATOR,
		INVALID_NODE_LAN_ID,
		UNKNOWN_ERROR
	}
	
	private Reason reason;
	private Throwable cause;

	public ExecutionException(Reason reason) {
		this.reason = reason;
	}
	
	public ExecutionException(Throwable cause) {
		this.reason = Reason.UNKNOWN_ERROR;
		this.cause = cause;
	}
	
	public ExecutionException(Reason reason, Throwable cause) {
		this.reason = reason;
		this.cause = cause;
	}
	
	public ExecutionException.Reason getReason() {
		return reason;
	}
	
	public Throwable getCause() {
		return cause;
	}
}
