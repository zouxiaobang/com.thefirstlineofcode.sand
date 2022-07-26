package com.thefirstlineofcode.sand.client.webcam;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.chalk.core.IChatServices;

public abstract class AbstractWatcher extends AbstractWebrtcPeer implements IWatcher {	
	public AbstractWatcher(IChatServices chatServices, JabberId peer) {
		super(chatServices, peer);
	}
	
	@Override
	public void watch() {		
		start();
	}
}
