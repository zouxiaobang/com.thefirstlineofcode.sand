package com.thefirstlineofcode.sand.client.core.actuator;

import com.thefirstlineofcode.sand.client.core.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.client.core.obx.IObxFactory;
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public interface IActuator {
	void setToConcentrator(IConcentrator concentrator, ITraceIdFactory traceIdFactory, IObxFactory obxFactory);
	<T> void registerExecutor(Class<T> actionType, Class<? extends IExecutor<T>> executorType);
	<T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory);
	boolean unregisterExecutor(Class<?> actionType);
	void setTraceIdFactory(ITraceIdFactory traceIdFactory);
	ITraceIdFactory getTraceIdFactory();
	void registerLanDeviceModel(ModelDescriptor modelDescriptor);
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
