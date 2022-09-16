package com.thefirstlineofcode.sand.client.webcam;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public abstract class AbstractWatcher extends AbstractWebrtcPeer implements IWatcher {
	protected boolean opened;
	protected List<IWatcher.Listener> watcherListeners;
	
	public AbstractWatcher(IChatServices chatServices, JabberId peer) {
		super(chatServices, peer);
		
		opened = false;
		watcherListeners = new ArrayList<>();
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
	
	protected void processPeerSignal(Iq iq, Signal.ID id, String data) {
		if (id == Signal.ID.OPENED) {
			opened();
		} else if (id == Signal.ID.CLOSED) {
			if (peer != null) {
				for (IWatcher.Listener watcherListener : watcherListeners) {
					watcherListener.beClosed();
				}
			}
			
			closed();
		} else {
			super.processPeerSignal(iq, id, data);
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
		if (peer != null)
			peer = null;
		
		opened = false;
	}
	
	@Override
	public boolean isClosed() {
		return !opened;
	}
	
	@Override
	public void watch() {
		if (!isStarted())
			start();
		
		open();
	}
	
	protected void open() {
		processSignal(Signal.ID.OPEN);
	}
	
	@Override
	public void close() {
		processSignal(Signal.ID.CLOSE);
		
		setPeer(null);
		opened = false;
	}
	
	@Override
	public void addWatcherListener(IWatcher.Listener watcherListener) {
		if (!watcherListeners.contains(watcherListener))
			watcherListeners.add(watcherListener);
	}
	
	@Override
	public boolean removeWatcherListener(IWatcher.Listener watcherListener) {
		return watcherListeners.remove(watcherListener);
	}
}
