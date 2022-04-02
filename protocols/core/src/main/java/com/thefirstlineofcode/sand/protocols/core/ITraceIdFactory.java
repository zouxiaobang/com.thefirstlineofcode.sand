package com.thefirstlineofcode.sand.protocols.core;

public interface ITraceIdFactory {
	ITraceId generateRequestId();
	ITraceId create(byte[] data);
}
