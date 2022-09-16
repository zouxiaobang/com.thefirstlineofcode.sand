package com.thefirstlineofcode.sand.client.webcam;

public interface IWebcam {
	void start();
	void stop();
	boolean isStarted();
	boolean isStopped();
	void open();
	void close();
	boolean isOpened();
	boolean isClosed();
}
