package com.thefirstlineofcode.sand.client.things.actuator;

import com.thefirstlineofcode.sand.client.things.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;

public interface IActuator {
	void setDeviceModel(String deivceModel);
	void setToConcentrator(IConcentrator concentrator, ITraceIdFactory traceIdFactory, IObmFactory obmFactory);
	<T> void registerExecutor(Class<T> actionType, Class<? extends IExecutor<T>> executorType);
	<T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory);
	boolean unregisterExecutor(Class<?> actionType);
	void setTraceIdFactory(ITraceIdFactory traceIdFactory);
	ITraceIdFactory getTraceIdFactory();
	void registerLanAction(Class<?> actionType);
	boolean unregisterLanAction(Class<?> actionType);
	void setLanExecuteTimeout(long timeout);
	long getLanExecuteTimeout();
	void setLanExecuteTimeoutCheckInterval(int interval);
	int getLanExecuteTimeoutCheckInterval();
	void start();
	void stop();
	boolean isStarted();
	boolean isStopped();
}
