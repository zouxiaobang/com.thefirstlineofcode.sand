package com.thefirstlineofcode.sand.client.core;

import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;

public interface IThing extends IDevice {
	void start();
	boolean isStarted();
	
	void stop() throws ExecutionException;
	boolean isStopped();
	
	void restart() throws ExecutionException;
	
	void shutdownSystem(boolean restart) throws ExecutionException;
}
