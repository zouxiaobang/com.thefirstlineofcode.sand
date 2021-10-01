package com.thefirstlineofcode.sand.client.things.autuator;

public class ExecutionException extends Exception {
	private static final long serialVersionUID = 7996516370515700261L;
	
	public enum Reason {
		UNSUPPORTED_ACTION_TYPE,
		FAILED_TO_CREATE_EXECUTOR_INSTANCE,
		NOT_A_CONCENTRATOR,
		INVALID_NODE_LAN_ID,
		UNSUPPORTED_COMMUNICATION_NET,
		FAILED_TO_CREATE_ACTION_DELIVERER_INSTANCE,
		FAILED_TO_DELIVER_ACTION_TO_NODE,
		BAD_ADDRESS,
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
	
	@Override
	public String getMessage() {
		if (reason != null)
			return reason.toString();
		else
			return null;
	}
}
