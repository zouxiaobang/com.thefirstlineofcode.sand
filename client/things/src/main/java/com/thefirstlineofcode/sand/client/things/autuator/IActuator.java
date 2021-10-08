package com.thefirstlineofcode.sand.client.things.autuator;

import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public interface IActuator {
	void registerModel(ModelDescriptor modelDescriptor);
	<T> void registerExecutor(Class<T> actionType, Class<? extends IExecutor<T>> executorType);
	<T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory);
	boolean unregisterExecutor(Class<?> actionType);
	void start();
	void stop();
}
