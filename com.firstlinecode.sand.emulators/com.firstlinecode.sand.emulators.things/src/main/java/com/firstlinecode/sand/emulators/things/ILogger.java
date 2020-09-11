package com.firstlinecode.sand.emulators.things;

public interface ILogger {
	String getName();
	void log(String message);
	void log(Exception e);
}
