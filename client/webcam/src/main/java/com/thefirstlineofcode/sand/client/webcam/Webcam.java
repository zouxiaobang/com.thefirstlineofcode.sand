package com.thefirstlineofcode.sand.client.webcam;

import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public class Webcam extends AbstractWebrtcPeer implements IWebcam, IWebrtcPeer.Listener {
	private boolean started;
	
	public Webcam(IChatServices chatServices) {
		super(chatServices);
		
		started = false;
	}
	
	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
		addListener(this);
		
		started = true;
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		removeListener(this);
		chatServices.getIqService().removeListener(Signal.PROTOCOL);
		
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
	public void offered(String offerSdp) {
		// TODO Auto-generated method stub
		System.out.println("Offered. SDP is: " + offerSdp);
	}

	@Override
	public void answered(String answerSdp) {
		// TODO Auto-generated method stub
		
	}

}
