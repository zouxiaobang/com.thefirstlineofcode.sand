package com.thefirstlineofcode.sand.client.webcam;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public interface IWebrtcPeer {
	public interface Listener {
		void offered(String offerSdp);
		void answered(String answerSdp);
		void iceCandidateFound(String candidate);
	}
	
	void start();
	void stop();
	
	JabberId getPeer();
	void sendToPeer(Signal signal);
	void addListener(Listener listener);
	boolean removeListener(Listener listener);
}
