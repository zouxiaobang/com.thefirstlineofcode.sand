package com.thefirstlineofcode.sand.server.kurento.webcam;

import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;

public class LiveStreamingSession {
	private static final Logger logger = LoggerFactory.getLogger(LiveStreamingSession.class);
	
	private JabberId watcher;
	private JabberId webcam;
	private MediaPipeline pipeline;
	private WebRtcEndpoint watcherEndpoint;
	private WebRtcEndpoint webcamEndpoint;
	
	public LiveStreamingSession(JabberId watcher, JabberId webcam) {
		this.watcher = watcher;
		this.webcam = webcam;
	}
	
	public JabberId getWatcher() {
		return watcher;
	}
	
	public JabberId getWebcam() {
		return webcam;
	}
	
	public MediaPipeline getPipeline() {
		if (pipeline == null)
			throw new IllegalStateException("Media pipeline not created yet.");
		
		return pipeline;
	}
	
	public WebRtcEndpoint getWatcherEndpoint() {
		if (pipeline == null)
			throw new IllegalStateException("Media pipeline not created yet.");
		
		return watcherEndpoint;
	}
	
	public WebRtcEndpoint getWebcamEndpoint() {
		if (pipeline == null)
			throw new IllegalStateException("Media pipeline not created yet.");
		
		return webcamEndpoint;
	}
	
	public boolean createPipeline(KurentoClient kClient) {
		try {
			pipeline = kClient.createMediaPipeline();
			
			watcherEndpoint = new WebRtcEndpoint.Builder(pipeline).build();
			webcamEndpoint = new WebRtcEndpoint.Builder(pipeline).build();
			
			webcamEndpoint.connect(watcherEndpoint);
			
			return true;
		} catch (Exception e) {
			logger.error("Failed to create kurento media pipeline.", e);
			destroy();
			
			return false;
		}
	}
	
	public void destroy() {
		if (webcamEndpoint != null)
			webcamEndpoint.release();
		if (watcherEndpoint != null)
			watcherEndpoint.release();
		
		if (pipeline != null)
			pipeline.release();
		
		webcam = null;
		watcher = null;
	}
	
}
