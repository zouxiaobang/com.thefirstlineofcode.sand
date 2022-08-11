package com.thefirstlineofcode.sand.client.webcam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public class Webcam extends AbstractWebrtcPeer implements IWebcam,
			IWebrtcPeer.Listener, IWebcamWebrtcNativeClient.Listener {
	private static final Logger logger = LoggerFactory.getLogger(Webcam.class);
	
	private boolean started;
	private boolean opened;
	
	private boolean notStartNativeService;
	private String nativeServicePath;
	
	private IWebcamWebrtcNativeClient nativeClient;
	
	public Webcam(IChatServices chatServices) {
		super(chatServices);
		
		started = false;
		opened = false;
		
		notStartNativeService = false;
	}
	
	public void setNativeServicePath(String nativeServicePath) {
		this.nativeServicePath = nativeServicePath;
	}
	
	public void setNotStartNativeService(boolean notStartNativeService) {
		this.notStartNativeService = notStartNativeService;
	}
	
	@Override
	public void start() {
		super.start();
		addListener(this);
		
		nativeClient = new WebcamWebrtcNativeClient(this);
		if (nativeServicePath != null)
			nativeClient.setNativeServicePath(nativeServicePath);
		
		if (!notStartNativeService) {
			logger.info("Try to start native service");
			
			nativeClient.startNativeService();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			logger.info("Native service has started.");
		}
		
		logger.info("Try to connect to native service.");
		nativeClient.connect();
		
		started = true;
		logger.info("Webcam has connected to native service.");
	}

	@Override
	public void stop() {
		super.stop();
		
		removeListener(this);
		
		if (nativeClient != null) {
			nativeClient.removeListener();
			nativeClient.stopNativeService();
			nativeClient = null;
		}
		
		opened = false;
		started = false;
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
	public void open() {
		if (!started)
			throw new IllegalStateException("Try to open webcam which's not in started state.");
		
		nativeClient.send("OPEN");
	}
	
	@Override
	public void close() {
		if (!isOpened())
			throw new IllegalStateException("Try to close webcam which's not in opened state.");
		
		nativeClient.send("CLOSE");
	}
	
	@Override
	public boolean isOpened() {
		return opened;
	}
	
	@Override
	public boolean isClosed() {
		return !opened;
	}

	@Override
	public void offered(String offerSdp) {
		nativeClient.send("OFFER " + offerSdp);
	}

	@Override
	public void answered(String answerSdp) {
		sendToPeer(new Signal(Signal.ID.ANSWER, answerSdp));
	}

	@Override
	public void processNativeMessage(String id, String data) {
		logger.info("Received a message from native service." +
				"Message ID: {}. Message data: {}.", id, data);
		// TODO Auto-generated method stub
		if ("CONFLICT".equals(id) && data == null) {
			started = false;
			stop();
			
			return;
		}
		
		if (!started)
			throw new IllegalStateException("Can't process native message. Not in started state.");
		
		if ("OPENED".equals(id) && data == null) {
			opened = true;
		} else if ("CLOSED".equals(id) && data == null) {
			opened = false;
		} else if (id.startsWith("ANSWER ") && data != null) {
			for (IWebrtcPeer.Listener listener : listeners) {
				listener.answered(data);
			}
		} else {
			throw new RuntimeException(
					"Received a message from native service." +
					" But it's not be understand." +
					String.format(" Message ID: %s. Message data: %s.", id, data));
		}
	}
}
