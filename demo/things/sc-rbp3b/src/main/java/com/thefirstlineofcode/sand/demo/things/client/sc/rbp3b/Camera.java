package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.UndefinedCondition;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Camera extends AbstractEdgeThing implements ICamera, CameraRtcSourcePeerClient.Listener,
			ICameraRtcSourcePeer.Listener {
	public static final String THING_TYPE = "Simple Camera";
	public static final String THING_MODEL = "SC-RBP3B";
	public static final String SOFTWARE_VERSION = "1.0.0-ALPHA1";
	
	private static final String SAND_DEMO_CONFIG_DIR = ".com.thefirstlineofcode.sand.demo";
	private static final String ATTRIBUTE_FILE_NAME = THING_MODEL + "-" + "attribute.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(Camera.class);
	
	private IActuator actuator;
	private String uploadUrl;
	private String downloadUrl;
	
	private ICameraRtcSourcePeer cameraRtcSourcePeer;
	
	public Camera() {
		this(null);
	}
	
	public Camera(StandardStreamConfig streamConfig) {
		super(THING_TYPE, THING_MODEL, streamConfig);
		uploadUrl = String.format("http://%s:8080", this.streamConfig.getHost());
		downloadUrl = String.format("http://%s:8080/files/", this.streamConfig.getHost());
	}
	
	@Override
	public void start() {
		int i = 0;
		while (!checkInternetConnectivity()) {
			i++;
			
			logger.info("No internet connection. Waiting for a while then trying again....");
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (i == 10) {
				logger.error("No internet connection. The thing can't be started.");
				throw new IllegalStateException("No internet connection. The program will exit.");
			}
		}
		
		try {
			super.start();
			
			CameraRtcSourcePeerClient cameraRtcSourcePeerClient = new CameraRtcSourcePeerClient(this);
			cameraRtcSourcePeer = cameraRtcSourcePeerClient.connect(this);
		} catch (Exception e) {
			logger.error("Some thing is wrong. The program can't run correctly.", e);
			
			throw new RuntimeException("Some thing is wrong. The program can't run correctly.", e);
		}
	}
	
	private boolean checkInternetConnectivity() {
		try {
			URL url = new URL("http://47.115.36.99");
			URLConnection connection = url.openConnection();
            connection.connect();
            
            return true;
		} catch (Exception e) {
			return false;
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
			private IExecutor<TakePhoto> executor = new TakePhotoExecutor();
			
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
	
	private class TakePhotoExecutor implements IExecutor<TakePhoto> {

		@Override
		public Object execute(Iq iq, TakePhoto takePhoto) throws ProtocolException {
			Response response = null;
			try {
				File photo = Camera.this.takePhoto(takePhoto);
				
				OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
				response = client.newCall(getPhotoUploadRequest(photo)).execute();
				if (response.code() != 200) {
					logger.error("Failed to upload photo. HTTP response status code: {}.", response.code());
					throw new ProtocolException(new UndefinedCondition(StanzaError.Type.CANCEL,
							ThingsUtils.getExecutionErrorDescription(getDeviceModel(), FAILED_TO_UPLOAD_PHOTO)));
				}
				
				return new TakePhoto(photo.getName(), downloadUrl + photo.getName());
			} catch (ExecutionException e) {
				logger.error(String.format("Exception is thrown when executing take photo action. Global action error code: %s.",
						ThingsUtils.getGlobalErrorCode(THING_MODEL, e.getErrorCode())), e);
				throw new ProtocolException(new UndefinedCondition(StanzaError.Type.CANCEL,
						ThingsUtils.getExecutionErrorDescription(getDeviceModel(), e.getErrorCode())));
			} catch (IOException e) {
				logger.error("Failed to upload photo.", e);
				throw new ProtocolException(new UndefinedCondition(StanzaError.Type.CANCEL,
						ThingsUtils.getExecutionErrorDescription(getDeviceModel(), FAILED_TO_UPLOAD_PHOTO)));
			} finally {
				if (response != null)
					response.close();
			}
		}
		
		private Request getPhotoUploadRequest(File photo) {
			RequestBody requestBody = new MultipartBody.Builder().
					setType(MultipartBody.FORM).
					addFormDataPart("file", photo.getName(), RequestBody.create(
							photo, MediaType.parse("application/octet-stream"))).
					build();
			
			return new Request.Builder().url(uploadUrl).post(requestBody).build();
		}
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
	public File takePhoto(TakePhoto takePhoto) throws ExecutionException {
		String photoPath = getOutputPath();
		int prepareTime = takePhoto.getPrepareTime() == null ? 5000 : takePhoto.getPrepareTime();
		runInNewProcess(getTakePhotoCmdArray(photoPath, prepareTime));
		
		File photo = new File(photoPath);
		if (!photo.exists()) {
			logger.error("Photo file wasn't taken. Photo path: " + photoPath + ".");
			throw new ExecutionException(ERROR_CODE_PHOTO_WAS_NOT_TAKEN);
		}
		
		logger.info("Photo was taken. Photo path: " + photoPath + ".");
		return photo;
	}
	
	private String[] getTakePhotoCmdArray(String photoPath, int prepareTime) {
		List<String> cmdList = new ArrayList<>();
		cmdList.add("libcamera-jpeg");
		cmdList.add("-t");
		cmdList.add(Integer.toString(prepareTime));
		cmdList.add("--width");
		cmdList.add("800");
		cmdList.add("--height");
		cmdList.add("600");
		cmdList.add("-q");
		cmdList.add("90");
		cmdList.add("-o");
		cmdList.add(photoPath);
		
		String[] cmdArray = cmdList.toArray(new String[0]);
		return cmdArray;
	}

	private String getOutputPath() {
		Calendar calendar = Calendar.getInstance();
		String fileName = String.format("/home/pi/tmp/%s-%s-%s-%s-%s-%s.jpg",
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.MILLISECOND));
		
		return fileName;
	}
	
	@Override
	public void stop() {
		if (cameraRtcSourcePeer != null && cameraRtcSourcePeer.isConnected())
			cameraRtcSourcePeer.close();
		
		super.stop();
	}

	@Override
	public void processException(CameraRtcSourcePeerException e) {
		logger.error("Camera RTC source peer excption occurred.", e);
		
		cameraRtcSourcePeer.close();
		
		try {
			logger.info("Try to reconnect to camera RTC source peer.");
			cameraRtcSourcePeer = new CameraRtcSourcePeerClient(this).connect(this);
		} catch (IOException ioe) {
			logger.error("Can't reconnect to camera RTC source peer. The device will stop.", ioe);
			stop();
		}
	}
	
}
