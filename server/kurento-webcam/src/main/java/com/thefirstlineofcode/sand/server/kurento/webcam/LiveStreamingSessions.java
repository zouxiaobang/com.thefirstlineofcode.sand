package com.thefirstlineofcode.sand.server.kurento.webcam;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;

public class LiveStreamingSessions {
	private static volatile LiveStreamingSessions instance;
	
	private Map<JabberId, LiveStreamingSession> sessions;
	private Map<JabberId, JabberId> webcamToWatchers;
	
	private LiveStreamingSessions() {
		sessions = new HashMap<>();
		webcamToWatchers = new HashMap<>();
	}
	
	public static LiveStreamingSessions getInstance() {
		if (instance != null)
			return instance;
		
		synchronized (LiveStreamingSessions.class) {
			if (instance != null)
				return instance;
			
			instance = new LiveStreamingSessions();
			return instance;
		}
	}
	
	public boolean createSession(JabberId watcher, JabberId webcam) {
		if (sessions.containsKey(watcher))
			return false;
		
		sessions.put(watcher, new LiveStreamingSession(watcher, webcam));
		webcamToWatchers.put(webcam, watcher);
		
		return true;
	}
	
	public LiveStreamingSession removeSession(JabberId watcher) {
		LiveStreamingSession session = sessions.remove(watcher);
		if (session == null)
			return null;
		
		webcamToWatchers.remove(session.getWebcam());
		
		return session;
	}
	
	public LiveStreamingSession getSessionByWatcher(JabberId watcher) {
		return sessions.get(watcher);
	}
	
	public LiveStreamingSession getSessionByWebcam(JabberId webcam) {
		JabberId watcher = webcamToWatchers.get(webcam);
		if (watcher == null)
			return null;
		
		return sessions.get(watcher);
	}
}
