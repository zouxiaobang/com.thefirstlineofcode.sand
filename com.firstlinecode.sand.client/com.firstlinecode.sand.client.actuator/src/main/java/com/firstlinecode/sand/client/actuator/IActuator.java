package com.firstlinecode.sand.client.actuator;

public interface IActuator {
	<T> void registerExecutor(Class<T> actionType, Class<IExecutor<T>> executorType);
	<T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory);
	boolean unregisterExecutor(Class<?> actionType);
	void start();
	void stop();
}
