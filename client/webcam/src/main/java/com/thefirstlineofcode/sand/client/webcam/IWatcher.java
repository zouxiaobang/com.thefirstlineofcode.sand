package com.thefirstlineofcode.sand.client.webcam;

public interface IWatcher {
	public interface Listener {
		void beClosed();
	}
	
	void watch();
	void close();
	void opened();
	boolean isOpened();
	void closed();
	boolean isClosed();
	void addWatcherListener(IWatcher.Listener watcherListener);
	boolean removeWatcherListener(IWatcher.Listener watcherListener);
}
