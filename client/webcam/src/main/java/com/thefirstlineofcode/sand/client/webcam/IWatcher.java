package com.thefirstlineofcode.sand.client.webcam;

public interface IWatcher {
	void watch();
	void close();
	void opened();
	boolean isOpened();
	void closed();
	boolean isClosed();
}
