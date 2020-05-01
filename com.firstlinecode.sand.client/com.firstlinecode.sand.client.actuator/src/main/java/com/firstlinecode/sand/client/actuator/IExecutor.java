package com.firstlinecode.sand.client.actuator;

public interface IExecutor<T> {
	void execute(T action) throws ExecutionException;
}
