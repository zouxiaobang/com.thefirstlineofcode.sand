package com.thefirstlineofcode.sand.client.core.actuator;

public interface IExecutorFactory<T> {
	IExecutor<T> create();
}
