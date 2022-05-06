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
	void registerLanAction(Class<?> lanActionType);
	void registerLanExecutionErrorProcessor(ILanExecutionErrorProcessor lanActionErrorProcessor);
	boolean unregisterLanAction(Class<?> actionType);
	void setDefaultLanExecutionTimeout(long timeout);
	long getDefaultLanExecutionTimeout();
	void setLanExecutionTimeoutCheckInterval(int interval);
	int getLanExecutionTimeoutCheckInterval();
	void setLanEnabled(boolean enabled);
	boolean isLanEnabled();
	void start();
	void stop();
	boolean isStarted();
	boolean isStopped();
}
