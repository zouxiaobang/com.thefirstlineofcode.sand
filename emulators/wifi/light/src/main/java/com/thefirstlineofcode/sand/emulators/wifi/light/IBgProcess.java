package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutorFactory;

public interface IBgProcess {
	void start();
	void stop();
	boolean isRegistered();
	boolean isConnected();
	<T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory);
	boolean unregisterExecutorFactory(Class<?> actionType);
	void addConnectionListener(IConnectionListener connectionListener);
	boolean removeConnectListener(IConnectionListener connectionListener);
	void addBgProcessListener(IBgProcessListener bgProcessListener);
	boolean removeBgProcessListener(IBgProcessListener bgProcessListener);
}
