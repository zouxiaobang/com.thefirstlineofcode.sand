package com.firstlinecode.sand.client.things.autuator;

public interface IExecutorFactory<T> {
	IExecutor<T> create() throws ExecutionException;
}
