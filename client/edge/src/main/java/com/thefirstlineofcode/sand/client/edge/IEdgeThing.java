package com.thefirstlineofcode.sand.client.edge;

import com.thefirstlineofcode.chalk.core.stream.StreamConfig;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.core.IDevice;

public interface IEdgeThing extends IDevice {
	StreamConfig getStreamConfig();
	void start();
	void stop();
	void restart();
	boolean isRegistered();
	void register();
	boolean isConnected();
	void connect();
	boolean isStarted();
	boolean isStopped();
	void addEdgeThingListener(IEdgeThingListener edgeThingListener);
	boolean removeEdgeThingListener(IEdgeThingListener edgeThingListener);
	void addConnectionListener(IConnectionListener connectionListener);
	boolean removeConnectionListener(IConnectionListener connectionListener);
}
