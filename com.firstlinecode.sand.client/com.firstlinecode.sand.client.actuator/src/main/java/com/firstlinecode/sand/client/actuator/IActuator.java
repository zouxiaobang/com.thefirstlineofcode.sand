package com.firstlinecode.sand.client.actuator;

public interface IActuator {
	<T> void registerExecutor(Class<T> actionType, Class<IExecutor<T>> executorType);
	boolean unregisterExecutor(Class<?> actionType);
	void setDefaultExecutor(IExecutor<Object> defaultExecutor);
	void start();
	void stop();
}
