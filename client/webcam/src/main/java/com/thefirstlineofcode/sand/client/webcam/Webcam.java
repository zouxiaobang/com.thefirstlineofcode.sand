package com.thefirstlineofcode.sand.client.webcam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public class Webcam extends AbstractWebrtcPeer implements IWebcam,
			IWebrtcPeer.Listener, IWebcamWebrtcNativeClient.Listener,
			IWebcam.Listener {
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
		
		if (isOpened())
			close();
		
		if (nativeClient != null) {
			nativeClient.stopNativeService();
			nativeClient.removeListener();
			nativeClient = null;
		}
		
		removeListener(this);
		
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
		if (!started)
			throw new IllegalStateException("Try to open webcam which's not in started state.");
		
		if (!isOpened()) {
			logger.warn("Try to close webcam which's not in opened state.");
			return;
		}
		
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
		if (offerSdp.charAt(offerSdp.length() - 1) != '\n') {
			offerSdp += '\n';
		}
		
		nativeClient.send("OFFER " + offerSdp);
	}

	@Override
	public void processNativeMessage(String id, String data) {
		logger.info("Received a message from native service." +
				"Message ID: {}. Message data: {}.", id, data);
		if ("CONFLICT".equals(id) && data == null) {
			started = false;
			stop();
			
			throw new RuntimeException("Can't connect to native service. Conflicted!");
		}
		
		if (!started)
			throw new IllegalStateException("Can't process native message. Not in started state.");
		
		if ("OPENED".equals(id) && data == null) {
			opened = true;
			sendToPeer(new Signal(Signal.ID.OPENED));
		} else if ("CLOSED".equals(id) && data == null) {
			opened = false;
			sendToPeer(new Signal(Signal.ID.CLOSED));
		} else if ("ANSWER".equals(id) && data != null) {
			sendToPeer(new Signal(Signal.ID.ANSWER, data));
		}  else if ("ICE_CANDIDATE_FOUND".equals(id) && data != null) {
			sendToPeer(new Signal(Signal.ID.ICE_CANDIDATE_FOUND, data));
		} else {
			throw new RuntimeException(
					"Received a message from native service." +
					" But the message can't be understanded." +
					String.format(" Message ID: %s. Message data: %s.", id, data));
		}
	}

	@Override
	public void answered(String answerSdp) {
		throw new IllegalStateException("Webcam received a answer SDP from the peer. Why???");
	}

	@Override
	public void iceCandidateFound(String jsonCandidate) {
		nativeClient.send("ICE_CANDIDATE_FOUND " + jsonCandidate);
	}

	@Override
	public void askToOpen() {
		nativeClient.send("OPEN");
	}

	@Override
	public void askToClose() {
		nativeClient.send("CLOSE");
	}
	
	protected void processPeerSignal(Signal.ID id, String data) {
		if (id == Signal.ID.OPEN) {
			logger.info("Received OPEN signal.");
			askToOpen();
		} else if (id == Signal.ID.CLOSE) {
			logger.info("Received CLOSE signal.");
			askToClose();
		} else {
			super.processPeerSignal(id, data);
		}
		
	}
}
