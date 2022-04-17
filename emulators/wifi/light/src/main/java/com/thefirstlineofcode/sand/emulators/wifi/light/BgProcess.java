package com.thefirstlineofcode.sand.emulators.wifi.light;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.chalk.core.AuthFailureException;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.StandardChatClient;
import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.core.stream.UsernamePasswordToken;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.actuator.ActuatorPlugin;
import com.thefirstlineofcode.sand.client.ibdr.IRegistration;
import com.thefirstlineofcode.sand.client.ibdr.IbdrPlugin;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.client.things.actuator.IActuator;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutorFactory;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.devices.light.Flash;

public class BgProcess implements IBgProcess, IConnectionListener {
	private List<IConnectionListener> connectionListeners;
	private List<IBgProcessListener> bgProcessListeners;
	
	private Light light;
	private StandardStreamConfig streamConfig;
	
	private IChatClient chatClient;
	private Thread autoReconnectThread;
	private IActuator actuator;
	
	private boolean started;
	private boolean stopToReconnect;
	
	public BgProcess(Light light, StandardStreamConfig streamConfig) {
		if (light == null)
			throw new IllegalArgumentException("Null light.");
		
		if (streamConfig == null)
			throw new IllegalArgumentException("Null stream config.");
		
		if (light.getDeviceId() == null)
			throw new IllegalArgumentException("Null device ID.");
		
		connectionListeners = new ArrayList<>();
		bgProcessListeners = new ArrayList<>();		
		
		this.light = light;
		this.streamConfig = streamConfig;
		this.started = false;
		this.stopToReconnect = true;
	}

	@Override
	public boolean isRegistered() {
		return light.getDeviceIdentity() != null;
	}

	@Override
	public synchronized boolean isConnected() {
		return chatClient != null && chatClient.isConnected();
	}
	
	@Override
	public <T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory) {
		
		
	}
	
	@Override
	public boolean unregisterExecutorFactory(Class<?> actionType) {
		// TODO Auto-generated method stub
		return false;
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
		if (started)
			stop();
		
		if (light.getDeviceIdentity() == null) {
			register();
		}
		
		if (light.getDeviceIdentity() == null)
			return;
		
		synchronized (this) {
			connect();
			if (isConnected()) {
				started = true;
				
				for (IBgProcessListener bgProcessListener : bgProcessListeners) {
					bgProcessListener.connected(chatClient);
				}
				
				startActuator();
			}			
		}
		
		startAutoReconnectThread();
	}

	private void startActuator() {
		if (actuator == null) {
			actuator = createActuator();
		}
		
		actuator.start();
	}

	private IActuator createActuator() {
		IActuator actuator = chatClient.createApi(IActuator.class);
		actuator.setDeviceModel(light.getThingModel());
		actuator.registerExecutorFactory(Flash.class, new IExecutorFactory<Flash>() {
			private IExecutor<Flash> executor = new FlashExecutor(light);
			
			@Override
			public IExecutor<Flash> create() {
				return executor;
			}
			
		});
		
		return actuator;
	}
	
	private void stopActuator() {
		if (actuator != null) {
			actuator.stop();
			actuator = null;
		}
	}

	protected void connect() {
		if (chatClient == null)
			chatClient = createChatClient();
		
		for (IConnectionListener connectionListener : connectionListeners) {
			chatClient.getConnection().addListener(connectionListener);
		}
		chatClient.getConnection().addListener(this);
		
		try {
			chatClient.connect(new UsernamePasswordToken(light.getDeviceIdentity().getDeviceName().toString(),
					light.getDeviceIdentity().getCredentials()));
		} catch (ConnectionException e) {
			for (IBgProcessListener bgProcessListener : bgProcessListeners) {
				bgProcessListener.FailedToConnect(e);
			}
		} catch (AuthFailureException e) {
			for (IBgProcessListener bgProcessListener : bgProcessListeners) {
				bgProcessListener.failedToAuth();
			}
		}
	}
	
	private void startAutoReconnectThread() {
		if (autoReconnectThread == null || !autoReconnectThread.isAlive()) {
			autoReconnectThread = new Thread(new AutoReconnectThread(), "Background Process Auto Reconnect Thread");			
		}
		
		stopToReconnect = false;
		autoReconnectThread.start();
	}
	
	private IChatClient createChatClient() {
		IChatClient chatClient = new StandardChatClient(createStreamConfigWithResource());
		chatClient.register(ActuatorPlugin.class);
		
		return chatClient;
	}
	
	private StandardStreamConfig createStreamConfigWithResource() {
		StandardStreamConfig cloned = new StandardStreamConfig(streamConfig.getHost(), streamConfig.getPort());
		cloned.setTlsPreferred(streamConfig.isTlsPreferred());
		cloned.setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
		
		return cloned;
	}

	private void register() {
		IChatClient chatClient = new StandardChatClient(streamConfig);
		chatClient.register(IbdrPlugin.class);
		
		IRegistration registration = chatClient.createApi(IRegistration.class);
		for (IConnectionListener connectionListener : connectionListeners) {			
			registration.addConnectionListener(connectionListener);
		}
		
		try {
			DeviceIdentity deviceIdentity = registration.register(light.getDeviceId());
			
			for (IBgProcessListener bgProcessListener : bgProcessListeners) {
				bgProcessListener.registered(deviceIdentity);
			}
		} catch (RegistrationException e) {
			for (IBgProcessListener bgProcessListener : bgProcessListeners) {
				bgProcessListener.registerExceptionOccurred(e);
			}
		} finally {			
			for (IConnectionListener connectionListener : connectionListeners) {			
				registration.removeConnectionListener(connectionListener);
			}
			chatClient.close();
		}
	}
	
	@Override
	public void stop() {
		stopAutoReconnectThread();
		
		synchronized (this) {			
			stopActuator();
			disconnect();
		}
		
		started = false;
	}

	private void stopAutoReconnectThread() {
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
		
		for (IBgProcessListener bgProcessListener : bgProcessListeners) {
			bgProcessListener.disconnected();
		}
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
	public void addConnectionListener(IConnectionListener connectionListener) {
		if (!connectionListeners.contains(connectionListener))
			connectionListeners.add(connectionListener);
	}

	@Override
	public boolean removeConnectionListener(IConnectionListener connectionListener) {
		return connectionListeners.remove(connectionListener);
	}
	
	private class AutoReconnectThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (stopToReconnect)
					break;
				
				synchronized (BgProcess.this) {
					if (!isConnected())
						connect();
					
					if (isConnected())
						startActuator();
				}
				
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public synchronized void exceptionOccurred(ConnectionException exception) {
		stopActuator();
		disconnect();
	}

	@Override
	public void messageReceived(String message) {}

	@Override
	public void heartBeatsReceived(int length) {}

	@Override
	public void messageSent(String message) {}

}
