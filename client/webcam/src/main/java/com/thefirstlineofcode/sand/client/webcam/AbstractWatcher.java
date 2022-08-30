package com.thefirstlineofcode.sand.client.webcam;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public abstract class AbstractWatcher extends AbstractWebrtcPeer implements IWatcher {
	protected boolean opened;
	
	public AbstractWatcher(IChatServices chatServices, JabberId peer) {
		super(chatServices, peer);
		opened = false;
	}
	
	@Override
	protected void processSignal(Signal.ID signalId, String data) {
		if (signalId != Signal.ID.OPEN &&
				signalId != Signal.ID.CLOSE &&
				signalId != Signal.ID.OFFER &&
				signalId != Signal.ID.ICE_CANDIDATE_FOUND)
			throw new IllegalArgumentException(String.format("Signal '%s' shouldn't occurred on watcher.", signalId));
		
		super.processSignal(signalId, data);
	}
	
	protected void processPeerSignal(Signal.ID id, String data) {
		if (id == Signal.ID.OPENED) {
			opened();
		} else if (id == Signal.ID.CLOSED) {
			closed();
		} else {
			super.processPeerSignal(id, data);
		}
		
	}
	
	@Override
	public void opened() {
		opened = true;
	}
	
	@Override
	public boolean isOpened() {
		return opened;
	}
	
	@Override
	public void closed() {
		opened = false;
	}
	
	@Override
	public boolean isClosed() {
		return !opened;
	}
}
