package com.thefirstlineofcode.sand.client.edge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAllowed;
import com.thefirstlineofcode.chalk.core.AuthFailureException;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.StandardChatClient;
import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.core.stream.UsernamePasswordToken;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.core.AbstractDevice;
import com.thefirstlineofcode.sand.client.ibdr.IRegistration;
import com.thefirstlineofcode.sand.client.ibdr.IbdrPlugin;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public abstract class AbstractEdgeThing extends AbstractDevice implements IEdgeThing, IConnectionListener {
	protected static final String ATTRIBUTE_NAME_STREAM_CONFIG = "stream_config";
	protected static final String ATTRIBUTE_NAME_DEVICE_IDENTITY = "device_identity";
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractEdgeThing.class);
	
	protected StandardStreamConfig streamConfig;
	protected DeviceIdentity identity;
	
	protected IChatClient chatClient;
	protected Thread autoReconnectThread;
	
	protected List<IEdgeThingListener> edgeThingListeners;
	protected List<IConnectionListener> connectionListeners;
	
	protected boolean started;
	protected boolean stopToReconnect;
	
	protected ConsoleThread consoleThread;
	
	public AbstractEdgeThing(String type, String model) {
		this(type, model, null);
	}
	
	public AbstractEdgeThing(String type, String model, StandardStreamConfig streamConfig) {
		super(type, model);
		
		this.streamConfig = streamConfig;
		
		powered = true;
		batteryPower = 100;
		
		if (this.streamConfig == null) {
			this.streamConfig = getStreamConfig(attributes);
		} else {
			attributes.put(ATTRIBUTE_NAME_STREAM_CONFIG, getStreamConfigString());
			attributesChanged = true;
		}
		
		identity = getDeviceIdentity(attributes);
		
		logger.info("I'm an edge thing[device_id='{}', host='{}', port='{}', tls_preferred='{}'].",
				deviceId, this.streamConfig.getHost(), this.streamConfig.getPort(), this.streamConfig.isTlsPreferred());
		
		edgeThingListeners = new ArrayList<>();
		connectionListeners = new ArrayList<>();
		
		started = false;
		stopToReconnect = true;
		
		if (attributesChanged)
			saveAttributes(attributes);
	}
	
	private String getStreamConfigString() {
		return String.format("%s,%s,%s", streamConfig.getHost(), streamConfig.getPort(), streamConfig.isTlsPreferred() ? "true" : "false");
	}

	protected StandardStreamConfig getStreamConfig(Map<String, String> attributes) {
		String sStreamConfig = attributes.get(ATTRIBUTE_NAME_STREAM_CONFIG);
		if (sStreamConfig == null) {
			logger.error("Can't read stream config. Null stream config string.");
			throw new IllegalArgumentException("Can't read stream config. Null stream config string.");
		}
		
		StringTokenizer st = new StringTokenizer(sStreamConfig, ",");
		if (st.countTokens() != 3) {
			logger.error("Can't read stream config. Not a valid stream config string.");
			throw new IllegalArgumentException("Can't read stream config. Not a valid stream config string.");
		}
		
		StandardStreamConfig streamConfig = createStreamConfig(st);
		streamConfig.setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
		
		return streamConfig;
	}
	
	protected StandardStreamConfig createStreamConfig(StringTokenizer st) {
		String host = st.nextToken().trim();
		int port = Integer.parseInt(st.nextToken().trim());
		boolean tlsRequired = Boolean.parseBoolean(st.nextToken().trim());
		
		return new StandardStreamConfig(host, port, tlsRequired);
	}
	
	@Override
	public StandardStreamConfig getStreamConfig() {
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
		
		logger.info("The thing has started.");
		started = true;
		
		synchronized (this) {
			connect();
		}
		
		System.out.println("Starting console...");
		startConsoleThread();
	}
	
	private void startConsoleThread() {
		consoleThread = new ConsoleThread();
		new Thread(consoleThread, "Thing Console Thread").start();
	}
	
	private class ConsoleThread implements Runnable {
		private volatile boolean stop = false;
		
		@Override
		public void run() {
			printConsoleHelp();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					String command = readCommand(in);
					
					if (stop)
						break;
					
					if ("help".equals(command)) {
						printConsoleHelp();
					} else if ("exit".equals(command)) {
						stop();
					} else if ("restart".equals(command)) {
						restart();
					} else {
						System.out.println(String.format("Unknown command: '%s'", command));
						printConsoleHelp();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void printConsoleHelp() {
			System.out.println("Commands:");
			System.out.println("help        Display help information.");
			System.out.println("restart     Restart application.");
			System.out.println("exit        Exit applicatinon.");
			System.out.print("$");
		}
		
		private String readCommand(BufferedReader in) throws IOException {
			while (!in.ready()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (stop) {
					return null;
				}
			}
			
			return in.readLine();
		}
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
		
		logger.info("The thing tries to connect to server.");
		
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
			
			chatClient.close();
			
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
		stopToReconnect = false;
		if (autoReconnectThread != null && autoReconnectThread.isAlive())
			return;
		
		autoReconnectThread = new Thread(new AutoReconnectThread(),
				String.format("%s Auto Reconnect Thread", getDeviceName()));			
		
		autoReconnectThread.start();
	}

	protected void registered(DeviceIdentity identity) {
		attributes.put(ATTRIBUTE_NAME_DEVICE_IDENTITY, getDeviceIdentityString(identity));
		saveAttributes(attributes);
		
		this.identity = identity;
		
		logger.info("The thing has registered. Device name is '{}'.", identity.getDeviceName());
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
		
		logger.info("The thing has stopped.");
		started = false;
		
		stopConsoleThread();
	}

	private void stopConsoleThread() {
		if (consoleThread != null) {
			consoleThread.stop = true;
		}
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
		
		logger.info("The thing tries to register to server.");
		
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
			
			registered(identity);
			for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
				edgeThingListener.registered(identity);
			}
		} catch (RegistrationException e) {
			for (IEdgeThingListener edgeThingListener : edgeThingListeners) {
				edgeThingListener.registerExceptionOccurred(e);
			}
			registrationExceptionOccurred(e);
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
	
	private String getDeviceIdentityString(DeviceIdentity identity) {
		return String.format("%s,%s", identity.getDeviceName(), identity.getCredentials());
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
				
				synchronized (AbstractEdgeThing.this) {
					if (!isConnected()) {
						if (logger.isInfoEnabled())
							logger.info("The thing has disconnected. Try to reconnect to server....");
						
						connect();
					}
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
		if (logger.isInfoEnabled())
			logger.info("The thing has connected to server.");
		
		startIotComponents();
		startAutoReconnectThread();		
	}
	
	protected void FailedToConnect(ConnectionException e) {
		logger.error("The thing failed to connect to server.", e);
	}
	
	protected void failedToAuth(AuthFailureException e) {
		logger.error("The thing failed to auth to server.", e);
	}
	
	protected void disconnected() {
		if (logger.isInfoEnabled())
			logger.info("The thing has disconnected from server.");
	}
	
	protected void registrationExceptionOccurred(RegistrationException e) {
		logger.error("Registration exception occurred.", e);
	}
	
	protected DeviceIdentity getDeviceIdentity(Map<String, String> attributes) {
		String sDeviceIdentity = attributes.get(ATTRIBUTE_NAME_DEVICE_IDENTITY);
		if (sDeviceIdentity == null)
			return null;
		
		int commaIndex = sDeviceIdentity.indexOf(',');
		if (commaIndex == -1) {
			throw new IllegalArgumentException("Cant read device identity. Not a valid device identity string.");
		}
			
		DeviceIdentity identity = new DeviceIdentity();
		identity.setDeviceName(sDeviceIdentity.substring(0, commaIndex).trim());
		identity.setCredentials(sDeviceIdentity.substring(commaIndex + 1, sDeviceIdentity.length()).trim());
		
		return identity;
	}
	
	@Override
	public void shutdownSystem(boolean restart) throws ExecutionException {
		if (!isLinux()) {
			throw new ProtocolException(new NotAllowed("Shutdown system action only supported on linux platform."));
		}
		
		runInNewProcess(getShutdownCmdArray(restart));
	}

	private String[] getShutdownCmdArray(boolean restart) {
		List<String> cmdList = new ArrayList<>();
		cmdList.add("sudo");
		cmdList.add("shutdown");
		if (restart) {
			cmdList.add("-r");
		} else {
			cmdList.add("-h");
		}
		cmdList.add("now");
		
		String[] cmdArray = cmdList.toArray(new String[0]);
		return cmdArray;
	}

	protected void runInNewProcess(String[] cmdArray) {
		try {
			ProcessBuilder pb = new ProcessBuilder(cmdArray).
					redirectInput(Redirect.INHERIT).
					redirectError(Redirect.INHERIT).
					redirectOutput(Redirect.INHERIT);
			Map<String, String> env = pb.environment();
			for (String key : System.getenv().keySet()) {
				env.put(key, System.getenv(key));
			}
			
			Process process = pb.start();
			process.waitFor();
		} catch (IOException e) {
			throw new RuntimeException("Can't run runtime process.", e);
		} catch (InterruptedException e) {
			throw new RuntimeException("Runtime process execution error.", e);
		}
	}
	
	protected boolean isLinux() {
		return "Linux".equals(System.getProperty("os.name"));
	}

	@Override
	public IChatClient getChatClient() {
		return chatClient;
	}
	
	protected abstract void registerChalkPlugins();
	protected abstract void startIotComponents();
	protected abstract void stopIotComponents();
}
