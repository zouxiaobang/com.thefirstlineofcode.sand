package com.thefirstlineofcode.sand.emulators.wifi.light;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.stream.StreamConfig;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutorFactory;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class BgProcess implements IBgProcess {
	private List<IConnectionListener> connectionListeners;
	private List<IBgProcessListener> bgProcessListeners;
	
	private StreamConfig streamConfig;
	private DeviceIdentity identity;
	private IChatClient chatClient;
	
	public BgProcess(StreamConfig streamConfig) {
		this(streamConfig, null);
	}
	
	public BgProcess(StreamConfig streamConfig, DeviceIdentity identity) {
		if (streamConfig == null)
			throw new IllegalArgumentException("Null stream config.");
		
		connectionListeners = new ArrayList<>();
		bgProcessListeners = new ArrayList<>();		
		
		this.streamConfig = streamConfig;
		this.identity = identity;
	}

	@Override
	public boolean isRegistered() {
		return identity != null;
	}

	@Override
	public boolean isConnected() {
		return chatClient != null && chatClient.isConnected();
	}

	@Override
	public <T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean unregisterExecutorFactory(Class<?> actionType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addConnectionListener(IConnectionListener connectionListener) {
		if (!connectionListeners.contains(connectionListener))
			connectionListeners.add(connectionListener);
	}

	@Override
	public boolean removeConnectListener(IConnectionListener connectionListener) {
		return connectionListeners.remove(connectionListener);
	}

	@Override
	public void addBgProcessListener(IBgProcessListener bgProcessListener) {
		if (!bgProcessListeners.contains(bgProcessListener))
			bgProcessListeners.add(bgProcessListener);
	}

	@Override
	public boolean removeBgProcessListener(IBgProcessListener bgProcessListener) {
		return bgProcessListeners.remove(bgProcessListener);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
