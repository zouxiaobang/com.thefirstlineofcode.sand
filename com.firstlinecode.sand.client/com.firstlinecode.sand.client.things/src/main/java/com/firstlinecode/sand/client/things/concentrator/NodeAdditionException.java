package com.firstlinecode.sand.client.things.concentrator;

public class NodeAdditionException extends Exception {
	private static final long serialVersionUID = -1164237073777410917L;
	
	public enum Reason {
		OVERFLOW_SIZE,
		REDUPLICATED_THING,
		ADDRESS_CONFLICT
	}
	
	public NodeAdditionException(Reason reason) {
		super();
	}
	
	public NodeAdditionException(Reason reason, String message) {
		super(message);
	}
}
