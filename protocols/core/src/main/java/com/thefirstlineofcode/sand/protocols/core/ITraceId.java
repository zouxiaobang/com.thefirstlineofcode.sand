package com.thefirstlineofcode.sand.protocols.core;

public interface ITraceId {
	public enum Type {
		REQUEST,
		RESPONSE,
		ERROR
	}
	
	byte[] getBytes();
	Type getType();
	boolean isResponse(byte[] responseId);
	boolean isError(byte[] errorId);
	ITraceId createResponseId();
	ITraceId createErrorId();
}
