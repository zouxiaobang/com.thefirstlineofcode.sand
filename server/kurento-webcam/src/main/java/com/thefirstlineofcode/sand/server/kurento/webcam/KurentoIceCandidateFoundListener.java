package com.thefirstlineofcode.sand.server.kurento.webcam;

import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public class KurentoIceCandidateFoundListener implements IEventListener<KurentoIceCandidateFoundEvent> {

	@Override
	public void process(IEventContext context, KurentoIceCandidateFoundEvent event) {
		Iq iceCandidateFoundSignal = new Iq(Iq.Type.SET, new Signal(Signal.ID.ICE_CANDIDATE_FOUND, event.getJsonCandidate()));
		iceCandidateFoundSignal.setFrom(event.getProvider());
		iceCandidateFoundSignal.setTo(event.getPeer());
		
		context.write(iceCandidateFoundSignal);
	}

}
