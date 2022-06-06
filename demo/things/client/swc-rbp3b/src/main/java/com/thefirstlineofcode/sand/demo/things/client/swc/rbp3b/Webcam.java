package com.thefirstlineofcode.sand.demo.things.client.swc.rbp3b;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.chalk.core.AuthFailureException;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.client.core.actuator.IActuator;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutorFactory;
import com.thefirstlineofcode.sand.client.edge.AbstractEdgeThing;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.client.things.simple.webcam.IWebcam;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.things.simple.webcam.TakePhoto;

public class Webcam extends AbstractEdgeThing implements IWebcam {
	public static final String THING_TYPE = "Simple Webcam";
	public static final String THING_MODEL = "SWC-RBP-3B";
	public static final String SOFTWARE_VERSION = "1.0.0-ALPHA1";
	
	private static final String SAND_DEMO_CONFIG_DIR = ".com.thefirstlineofcode.sand.demo";
	private static final String ATTRIBUTE_FILE_NAME = "attribute.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(Webcam.class);
	
	private IActuator actuator;
	
	public Webcam() {
		super(THING_TYPE, THING_MODEL);
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
	protected void registrationExceptionOccurred(RegistrationException e) {
		logger.error("Registration exception occurred.", e);
	}

	@Override
	protected void registerChalkPlugins() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void FailedToConnect(ConnectionException e) {
		logger.error("Failed to connect to server.", e);
	}

	@Override
	protected void failedToAuth(AuthFailureException e) {
		logger.error("Failed to auth.", e);

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
		
		return actuator;
	}

	@Override
	protected void stopIotComponents() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void disconnected() {
		if (logger.isWarnEnabled())
			logger.warn("Disconnected from server.");
	}

	@Override
	protected Map<String, String> loadDeviceAttributes() {
		File attributesFile = getAttributesFile();
		
		Properties properties = new Properties();
		
		Reader reader = null;
		try {
			reader = new BufferedReader(new FileReader(attributesFile));
			properties.load(reader);			
		} catch (Exception e) {
			logger.error(String.format("Can't load attributes from file '%s'.",
					attributesFile.getAbsolutePath()), e);
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
		File attributesFile = getAttributesFile();
		
		Properties properties = new Properties();
		for (String attributeName : attributes.keySet()) {
			properties.put(attributeName, attributes.get(attributeName));
		}
		
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(attributesFile));
			properties.store(writer, null);			
		} catch (Exception e) {
			logger.error(String.format("Can't save attributes to file '%s'.",
					attributesFile.getAbsolutePath()), e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private File getAttributesFile() {
		String userHome = System.getProperty("user.home");
		File attributesFile = new File(userHome, SAND_DEMO_CONFIG_DIR + "/" + ATTRIBUTE_FILE_NAME);
		
		return attributesFile;
	}

	@Override
	protected String generateDeviceId() {
		return THING_MODEL + "-" + ThingsUtils.generateRandomId(8);
	}
	
	@Override
	protected DeviceIdentity getIdentity(Map<String, String> attributes) {
		String sDeviceIdentity = attributes.get(ATTRIBUTE_NAME_DEVICE_IDENTITY);
		if (sDeviceIdentity == null)
			return null;
		
		int commaIndex = sDeviceIdentity.indexOf(',');
		if (commaIndex == -1)
			throw new IllegalArgumentException("Cant read device identity. Not a valid device identity string.");
			
		DeviceIdentity identity = new DeviceIdentity();
		identity.setDeviceName(sDeviceIdentity.substring(commaIndex).trim());
		identity.setCredentials(sDeviceIdentity.substring(commaIndex + 1, sDeviceIdentity.length()).trim());
		
		return identity;
	}

	@Override
	public void takePhoto() throws ExecutionException {
		// TODO Auto-generated method stub
		
	}

}
