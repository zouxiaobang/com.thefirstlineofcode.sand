package com.firstlinecode.sand.client.actuator;

public interface IExecutorFactory<T> {
	IExecutor<T> create() throws ExecutionException;
}
