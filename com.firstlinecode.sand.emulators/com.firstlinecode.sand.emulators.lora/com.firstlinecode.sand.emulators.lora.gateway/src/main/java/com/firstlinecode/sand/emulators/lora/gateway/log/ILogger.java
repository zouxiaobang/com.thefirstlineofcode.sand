package com.firstlinecode.sand.emulators.lora.gateway.log;

public interface ILogger {
	String getName();
	void log(String message);
	void log(Exception e);
}
