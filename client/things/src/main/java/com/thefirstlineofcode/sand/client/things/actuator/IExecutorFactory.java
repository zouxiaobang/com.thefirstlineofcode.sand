package com.thefirstlineofcode.sand.client.things.actuator;

public interface IExecutorFactory<T> {
	IExecutor<T> create();
}
