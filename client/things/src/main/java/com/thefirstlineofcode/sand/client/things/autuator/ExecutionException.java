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

	public ExecutionException(Reason reason) {
		this(reason, null, null);
	}
	
	public ExecutionException(Reason reason, String message) {
		this(reason, message, null);
	}
	
	public ExecutionException(Reason reason, Throwable cause) {
		this(reason, null, cause);
	}
	
	public ExecutionException(Reason reason, String message, Throwable cause) {
		super(message, cause);
		
		this.reason = reason;
	}
	
	public ExecutionException.Reason getReason() {
		return reason;
	}
}
