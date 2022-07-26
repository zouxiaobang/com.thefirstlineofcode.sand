package com.thefirstlineofcode.sand.client.webcam;

public class CameraWebrtcPeerException extends Exception {
	
	private static final long serialVersionUID = 8084394794131769574L;

	public CameraWebrtcPeerException(String message) {
		super(message);
	}
	
	public CameraWebrtcPeerException(String message, Throwable e) {
		super(message, e);
	}
	
	public CameraWebrtcPeerException(Throwable e) {
		super(e);
	}
}
