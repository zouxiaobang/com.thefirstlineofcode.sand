package com.thefirstlineofcode.sand.client.edge;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.chalk.core.AuthFailureException;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.StandardChatClient;
import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.core.stream.StreamConfig;
import com.thefirstlineofcode.chalk.core.stream.UsernamePasswordToken;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.core.AbstractDevice;
import com.thefirstlineofcode.sand.client.ibdr.IRegistration;
import com.thefirstlineofcode.sand.client.ibdr.IbdrPlugin;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public abstract class AbstractEdgeDevice extends AbstractDevice implements IEdgeThing, IConnectionListener {
	protected StandardStreamConfig streamConfig;
	protected DeviceIdentity identity;
	
	protected IChatClient chatClient;
	protected Thread autoReconnectThread;
	
	protected List<IEdgeThingListener> edgeThingListeners;
	protected List<IConnectionListener> connectionListeners;
	
	protected boolean started;
	protected boolean stopToReconnect;
	
	public AbstractEdgeDevice(String type, String model, StandardStreamConfig streamConfig) {
		super(type, model);
		
		powered = true;
		batteryPower = 100;
		
		if (streamConfig == null)
			throw new IllegalArgumentException("Null stream config.");
		this.streamConfig = streamConfig;
		
		edgeThingListeners = new ArrayList<>();
		connectionListeners = new ArrayList<>();
		
		started = false;
		stopToReconnect = true;
	}
	
	@Override
	public StreamConfig getStreamConfig() {
		return streamConfig;
	}

	@Override
	public void start() {
		if (!isPowered())
			return;
		
		if (started)
			stop();
		
		if (!isRegistered()) {
			register();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!isRegistered())
				return;
		}
		
		synchronized (this) {
			connect();
		}
		
		started = true;
	}

	@Override
	public void connect() {
		if (chatClient == null) {
			chatClient = createChatClient();
			registerChalkPlugins();
		}
		
		for (IConnectionListener connectionListener : connectionListeners) {
			chatClient.getConnection().addListener(connectionListener);
		}
		chatClient.getConnection().addListener(this);
		
		try {
			chatClient.connect(new UsernamePasswordToken(identity.getDeviceName().toString(),
					identity.getCredentials()));
			
			if (isConnected()) {
				for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
					edgeThingListener.connected(chatClient);
				}
				
				connected(chatClient);
			}
		} catch (ConnectionException e) {
			removeConnectionListenersFromChatClient(chatClient);
			
			for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
				edgeThingListener.FailedToConnect(e);
			}
			FailedToConnect(e);
		} catch (AuthFailureException e) {
			removeConnectionListenersFromChatClient(chatClient);
			
			for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
				edgeThingListener.failedToAuth(e);
			}
			failedToAuth(e);
		}
	}

	protected void removeConnectionListenersFromChatClient(IChatClient chatClient) {
		for (IConnectionListener connectionListener : connectionListeners) {
			chatClient.getConnection().removeListener(connectionListener);
		}		
		chatClient.getConnection().removeListener(this);
	}

	protected IChatClient createChatClient() {
		return new StandardChatClient(createStreamConfigWithResource());
	}

	protected StandardStreamConfig createStreamConfigWithResource() {
		StandardStreamConfig cloned = new StandardStreamConfig(streamConfig.getHost(), streamConfig.getPort());
		cloned.setTlsPreferred(streamConfig.isTlsPreferred());
		cloned.setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
		
		return cloned;
	}
	
	protected void startAutoReconnectThread() {
		if (autoReconnectThread == null || !autoReconnectThread.isAlive()) {
			autoReconnectThread = new Thread(new AutoReconnectThread(),
					String.format("%s Auto Reconnect Thread", getDeviceName()));			
		}
		
		stopToReconnect = false;
		autoReconnectThread.start();
	}

	protected void registered(DeviceIdentity identity) {
		saveDeviceIdentity(identity);
	}
	
	@Override
	public void stop() {
		if (!isPowered())
			return;
		
		if (!started)
			return;
		
		stopAutoReconnectThread();
		
		synchronized (this) {			
			stopIotComponents();
			disconnect();
		}
		
		started = false;
	}
	
	protected void disconnect() {
		if (isConnected()) {
			chatClient.close();
		}
		
		if (chatClient != null) {
			for (IConnectionListener connectionListener : connectionListeners)
				chatClient.getConnection().removeListener(connectionListener);
			chatClient.getConnection().removeListener(this);
		}
		
		if (chatClient != null)
			chatClient = null;
		
		for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
			edgeThingListener.disconnected();
		}
		disconnected();
	}

	protected void stopAutoReconnectThread() {
		stopToReconnect = true;
		while (autoReconnectThread != null &&
				autoReconnectThread.isAlive()) {			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		autoReconnectThread = null;
	}
	
	@Override
	public boolean isRegistered() {
		return identity != null;
	}
	
	@Override
	public void register() {
		IChatClient chatClient = new StandardChatClient(streamConfig);
		chatClient.register(IbdrPlugin.class);
		
		IRegistration registration = null;
		try {
			registration = chatClient.createApi(IRegistration.class);
			for (IConnectionListener listener : connectionListeners) {
				registration.addConnectionListener(listener);
			}
			registration.addConnectionListener(this);
			
			identity = registration.register(deviceId);
			if (identity == null)
				return;
			
			for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
				edgeThingListener.registered(identity);
			}
			registered(identity);
		} catch (RegistrationException e) {
			for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
				edgeThingListener.registerExceptionOccurred(e);
			}
			registerExceptionOccurred(e);
		} finally {
			if (registration != null) {
				for (IConnectionListener listener : connectionListeners) {
					registration.removeConnectionListener(listener);
				}
				registration.removeConnectionListener(this);
			}
			
			chatClient.close();
		}
	}
	
	@Override
	public synchronized boolean isConnected() {
		return chatClient != null && chatClient.isConnected();
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public boolean isStopped() {
		return !started;
	}
	
	@Override
	public void addEdgeThingListener(IEdgeThingListener edgeThingListener) {
		if (!edgeThingListeners.contains(edgeThingListener))
			edgeThingListeners.add(edgeThingListener);
	}
	
	@Override
	public boolean removeEdgeThingListener(IEdgeThingListener edgeThingListener) {
		return edgeThingListeners.remove(edgeThingListener);
	}
	
	@Override
	public void addConnectionListener(IConnectionListener connectionListener) {
		if (!connectionListeners.contains(connectionListener))
			connectionListeners.add(connectionListener);
	}

	@Override
	public boolean removeConnectionListener(IConnectionListener connectionListener) {
		return connectionListeners.remove(connectionListener);
	}
	
	@Override
	public void restart() {
		stop();
		start();
	}
	
	@Override
	public void messageReceived(String message) {}

	@Override
	public void heartBeatsReceived(int length) {}

	@Override
	public void messageSent(String message) {}
	
	@Override
	public void exceptionOccurred(ConnectionException exception) {
		disconnect();
	}
	
	private class AutoReconnectThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (stopToReconnect)
					break;
				
				synchronized (AbstractEdgeDevice.this) {
					if (!isConnected())
						connect();
				}
				
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void connected(IChatClient chatClient) {
		startIotComponents();
		startAutoReconnectThread();		
	}
	
	protected abstract void registerExceptionOccurred(RegistrationException e);
	protected abstract void saveDeviceIdentity(DeviceIdentity identity);
	protected abstract void registerChalkPlugins();
	protected abstract void FailedToConnect(ConnectionException e);
	protected abstract void failedToAuth(AuthFailureException e);
	protected abstract void startIotComponents();
	protected abstract void stopIotComponents();
	protected abstract void disconnected();
}
