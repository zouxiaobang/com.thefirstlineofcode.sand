package com.firstlinecode.sand.client.things.concentrator;

public class NodeCreationException extends Exception {
	private static final long serialVersionUID = -1164237073777410917L;
	
	public enum Reason {
		OVERFLOW_SIZE,
		REDUPLICATED_THING,
		ADDRESS_CONFLICT
	}
	
	public NodeCreationException(Reason reason) {
		super();
	}
	
	public NodeCreationException(Reason reason, String message) {
		super(message);
	}
}
