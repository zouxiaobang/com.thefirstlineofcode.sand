package com.thefirstlineofcode.sand.server.kurento.webcam;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;

public class KurentoIceCandidateFoundEvent implements IEvent {
	private JabberId provider;
	private JabberId peer;
	private String jsonCandidate;
	
	public KurentoIceCandidateFoundEvent(JabberId provider, JabberId peer, String jsonCandidate) {
		this.provider = provider;
		this.peer = peer;
		this.jsonCandidate = jsonCandidate;
	}
	
	public JabberId getProvider() {
		return provider;
	}
	
	public JabberId getPeer() {
		return peer;
	}
	
	public String getJsonCandidate() {
		return jsonCandidate;
	}
	
	@Override
	public Object clone() {
		return new KurentoIceCandidateFoundEvent(provider, peer, jsonCandidate);
	}
}
