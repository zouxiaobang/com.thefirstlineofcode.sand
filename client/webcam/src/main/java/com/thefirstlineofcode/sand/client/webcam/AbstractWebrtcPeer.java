package com.thefirstlineofcode.sand.client.webcam;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.ResourceConstraint;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.stanza.IIqListener;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public abstract class AbstractWebrtcPeer implements IWebrtcPeer, IIqListener {
	protected IChatServices chatServices;
	
	protected List<Listener> listeners;
	protected JabberId peer;
	
	public AbstractWebrtcPeer(IChatServices chatServices) {
		this(chatServices, null);
	}
	
	public AbstractWebrtcPeer(IChatServices chatServices, JabberId peer) {
		this.chatServices = chatServices;
		this.peer = peer;
		
		listeners = new ArrayList<>();
		
	}
	
	@Override
	public void start() {
		chatServices.getIqService().addListener(Signal.PROTOCOL, this);		
	}
	
	@Override
	public void stop() {
		chatServices.getIqService().removeListener(Signal.PROTOCOL);
		peer = null;
	}
	
	public void setPeer(JabberId peer) {
		this.peer = peer;
	}
	
	@Override
	public JabberId getPeer() {
		return peer;
	}

	@Override
	public void sendToPeer(Signal signal) {
		Iq iq = new Iq(Iq.Type.SET);
		iq.setTo(peer);
		iq.setObject(signal);
		
		chatServices.getIqService().send(iq);
	}
	
	@Override
	public void addListener(Listener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}
	
	protected void processSignal(String id, String data) {
		if (Signal.ID.OFFER.toString().equals(id)) {
			sendToPeer(new Signal(Signal.ID.OFFER, data));
		} else if (Signal.ID.ANSWER.toString().equals(id)) {
			sendToPeer(new Signal(Signal.ID.ANSWER, data));
		} else {
			throw new RuntimeException(String.format("Unknown signal ID: %s.", id));
		}
	}
	
	protected void processPeerSignal(Signal.ID id, String data) {
		if (Signal.ID.OFFER.equals(id)) {
			for (Listener listener : listeners) {
				listener.offered(data);
			}
		} else if (Signal.ID.ANSWER.equals(id)) {
			for (Listener listener : listeners) {
				listener.answered(data);
			}
		} else {
			throw new RuntimeException(String.format("Unknown signal ID: %s.", id));
		}
	}

	@Override
	public void received(Iq iq) {
		if (iq.getFrom() == null)
			throw new ProtocolException(new BadRequest("Null peer."));
		
		JabberId sender = iq.getFrom();
		if (peer == null)
			setPeer(sender);
		
		if (!sender.equals(peer)) {
			throw new ProtocolException(new ResourceConstraint());
		}
		
		Signal signal = iq.getObject();
		processPeerSignal(signal.getId(), signal.getData());
	}
}
