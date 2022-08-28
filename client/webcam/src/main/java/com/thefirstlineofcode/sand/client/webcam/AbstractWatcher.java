package com.thefirstlineofcode.sand.client.webcam;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public abstract class AbstractWatcher extends AbstractWebrtcPeer implements IWatcher {	
	public AbstractWatcher(IChatServices chatServices, JabberId peer) {
		super(chatServices, peer);
	}
	
	@Override
	public void watch() {		
		start();
	}
	
	@Override
	protected void processSignal(Signal.ID signalId, String data) {
		if (signalId != Signal.ID.OFFER &&
				signalId != Signal.ID.ICE_CANDIDATE_FOUND)
			throw new IllegalArgumentException(String.format("Signal '%s' shouldn't occurred on watcher.", signalId));
		
		super.processSignal(signalId, data);
	}
}
