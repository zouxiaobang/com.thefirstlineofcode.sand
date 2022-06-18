package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.sand.client.actuator.ActuatorPlugin;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.client.core.actuator.IActuator;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutorFactory;
import com.thefirstlineofcode.sand.client.core.actuator.RestartExecutor;
import com.thefirstlineofcode.sand.client.core.actuator.ShutdownSystemExecutor;
import com.thefirstlineofcode.sand.client.core.actuator.StopExecutor;
import com.thefirstlineofcode.sand.client.edge.AbstractEdgeThing;
import com.thefirstlineofcode.sand.client.edge.ResponseInAdvanceExecutor;
import com.thefirstlineofcode.sand.client.things.simple.camera.CameraPlugin;
import com.thefirstlineofcode.sand.client.things.simple.camera.ICamera;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Restart;
import com.thefirstlineofcode.sand.protocols.actuator.actions.ShutdownSystem;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Stop;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.TakePhoto;

public class Camera extends AbstractEdgeThing implements ICamera {
	public static final String THING_TYPE = "Simple Camera";
	public static final String THING_MODEL = "SC-RBP3B";
	public static final String SOFTWARE_VERSION = "1.0.0-ALPHA1";
	
	private static final String SAND_DEMO_CONFIG_DIR = ".com.thefirstlineofcode.sand.demo";
	private static final String ATTRIBUTE_FILE_NAME = THING_MODEL + "-" + "attribute.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(Camera.class);
	
	private IActuator actuator;
	
	public Camera() {
		this(null);
	}
	
	public Camera(StandardStreamConfig streamConfig) {
		super(THING_TYPE, THING_MODEL, streamConfig);
	}
	
	@Override
	public void start() {
		try {
			super.start();
		} catch (Exception e) {
			logger.error("Some thing is wrong. The program can't run correctly.", e);
			
			throw e;
		}
	}
	
	@Override
	public String getSoftwareVersion() {
		return SOFTWARE_VERSION;
	}
	
	@Override
	protected void registerChalkPlugins() {
		chatClient.register(ActuatorPlugin.class);
		chatClient.register(CameraPlugin.class);
	}
	
	@Override
	protected void startIotComponents() {
		startActuator();
	}
	
	protected void startActuator() {
		if (actuator == null) {
			actuator = createActuator();
		}
		
		actuator.start();
	}

	protected IActuator createActuator() {
		IActuator actuator = chatClient.createApi(IActuator.class);
		actuator.registerExecutorFactory(TakePhoto.class, new IExecutorFactory<TakePhoto>() {
			private IExecutor<TakePhoto> executor = new IExecutor<TakePhoto>() {
				
				@Override
				public void execute(Iq iq, TakePhoto action) throws ProtocolException {
					// TODO Auto-generated method stub
					System.out.println("You should take a photo.");
				}
			};
			
			@Override
			public IExecutor<TakePhoto> create() {
				return executor;
			}
			
		});
		
		actuator.registerExecutorFactory(Stop.class, new IExecutorFactory<Stop>() {
			@Override
			public IExecutor<Stop> create() {
				return new ResponseInAdvanceExecutor<Stop>(new StopExecutor(Camera.this), Camera.this);
			}
		});
		
		actuator.registerExecutorFactory(Restart.class, new IExecutorFactory<Restart>() {
			@Override
			public IExecutor<Restart> create() {
				return new ResponseInAdvanceExecutor<Restart>(new RestartExecutor(Camera.this), Camera.this);
			}		
		});
		
		actuator.registerExecutorFactory(ShutdownSystem.class, new IExecutorFactory<ShutdownSystem>() {
			@Override
			public IExecutor<ShutdownSystem> create() {
				return new ResponseInAdvanceExecutor<ShutdownSystem>(new ShutdownSystemExecutor(Camera.this), Camera.this);
			}		
		});
		
		return actuator;
	}
	
	@Override
	protected void stopIotComponents() {
		if (actuator != null) {
			actuator.stop();
			actuator = null;
		}		
	}
	
	@Override
	protected Map<String, String> loadDeviceAttributes() {
		Path attributesFilePath = getAttributesFilePath();
		
		if (!Files.exists(attributesFilePath, LinkOption.NOFOLLOW_LINKS)) {
			logger.info("Attributes file not existed. Ignore to load attributes.");
			
			return null;
		}
		
		Properties properties = new Properties();
		Reader reader = null;
		try {
			reader = Files.newBufferedReader(attributesFilePath);
			properties.load(reader);			
		} catch (Exception e) {
			logger.error(String.format("Can't load attributes from file '%s'.",
					attributesFilePath.toAbsolutePath()), e);
			throw new RuntimeException(String.format("Can't load attributes from file '%s'.",
					attributesFilePath.toAbsolutePath()), e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		Map<String, String> attributes = new HashMap<>();
		for (String propertyName : properties.stringPropertyNames()) {
			attributes.put(propertyName, properties.getProperty(propertyName));
		}
		
		return attributes;
	}
	
	@Override
	protected void saveAttributes(Map<String, String> attributes) {
		Properties properties = new Properties();
		for (String attributeName : attributes.keySet()) {
			properties.put(attributeName, attributes.get(attributeName));
		}
		
		Path attributesFilePath = getAttributesFilePath();
		Path attributesBakFilePath = Paths.get(attributesFilePath.getParent().toAbsolutePath().toString(), attributesFilePath.toFile().getName() + ".bak");
		if (Files.exists(attributesFilePath, LinkOption.NOFOLLOW_LINKS)) {			
			try {
				Files.move(attributesFilePath, attributesBakFilePath);
			} catch (IOException e) {
				logger.error("Can't backup attributes file.", e);
				throw new RuntimeException("Can't backup attributes file.", e);
			}
		}
		
		if (!Files.exists(attributesFilePath.getParent(), LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(attributesFilePath.getParent());
			} catch (IOException e) {
				logger.error(String.format("Can't create directory %s.", attributesFilePath.getParent().toAbsolutePath()), e);
				throw new RuntimeException(String.format("Can't create directory %s.", attributesFilePath.getParent().toAbsolutePath()), e);
			}
		}
		
		Writer writer = null;
		try {
			writer = Files.newBufferedWriter(attributesFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			properties.store(writer, null);
			
			logger.info(String.format("Attributes are saved to %s.", attributesFilePath.toAbsolutePath()));
		} catch (Exception e) {
			logger.error(String.format("Can't save attributes to file '%s'.",
					attributesFilePath.toAbsolutePath()), e);
			throw new RuntimeException(String.format("Can't save attributes to file '%s'.",
					attributesFilePath.toAbsolutePath()), e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		if (Files.exists(attributesBakFilePath, LinkOption.NOFOLLOW_LINKS)) {			
			try {
				Files.delete(attributesBakFilePath);
			} catch (IOException e) {
				logger.error("Can't delete attributes backup file.", e);
				throw new RuntimeException("Can't delete attributes backup file.", e);
			}
		}
	}
	
	private Path getAttributesFilePath() {
		String userHome = System.getProperty("user.home");
		Path attributesFilePath = Paths.get(userHome, SAND_DEMO_CONFIG_DIR + "/" + ATTRIBUTE_FILE_NAME);
		
		return attributesFilePath;
	}

	@Override
	protected String generateDeviceId() {
		return THING_MODEL + "-" + ThingsUtils.generateRandomId(8);
	}

	@Override
	public void takePhoto() throws ExecutionException {
		// TODO Auto-generated method stub
		
	}
}
