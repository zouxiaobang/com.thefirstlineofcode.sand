package com.thefirstlineofcode.sand.server.kurento.webcam;

import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.KurentoClient;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAllowed;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.RecipientUnavailable;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.auth.IAccountManager;
import com.thefirstlineofcode.granite.framework.core.connection.IConnectionContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.granite.framework.core.session.ISessionListener;
import com.thefirstlineofcode.granite.framework.im.IResourcesService;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class SignalProcessor implements IXepProcessor<Iq, Signal>, ISessionListener {
	private static final String KEY_LIVE_STREAMING_SESSION = "live-streaming-session";
	
	@BeanDependency
	private IAccountManager accountManager;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private KurentoClient kClient;
	
	@BeanDependency
	private IResourcesService resourcesService;
	
	private WebcamEndpointErrorListener webcamEndpointErrorListener;
	private WebcamEndpointIceCandidateFoundListener webcamEndpointIceCandidateFoundListener;
	private WatcherEndpointErrorListener watcherEndpointErrorListener;
	private WatcherEndpointIceCandidateFoundListener watcherEndpointIceCandidateFoundListener;
	
	public SignalProcessor() {
		webcamEndpointErrorListener = new WebcamEndpointErrorListener();
		webcamEndpointIceCandidateFoundListener = new WebcamEndpointIceCandidateFoundListener();
		
		watcherEndpointErrorListener = new WatcherEndpointErrorListener();
		watcherEndpointIceCandidateFoundListener = new WatcherEndpointIceCandidateFoundListener();
	}
	
	@Override
	public void process(IProcessingContext context, Iq iq, Signal xep) {
		JabberId sessionJid = context.getJid();
		boolean isUserSession = false;
		if (accountManager.exists(sessionJid.getNode())) {
			isUserSession = true;
		} else if (deviceManager.deviceNameExists(sessionJid.getNode())) {
			isUserSession = false;
		} else {			
			throw new ProtocolException(new NotAllowed(String.format("Neither user nor device. What thing are you?")));
		}
		
		if (iq.getTo() == null)
			throw new ProtocolException(new BadRequest("Null peer JID."));
		
		if (isUserSession) {
			if (!deviceManager.deviceNameExists(iq.getTo().getNode())) {
				throw new ProtocolException(new ItemNotFound(String.format("Device named '%s' doesn't exist.", iq.getTo().getNode())));
			}			
			
			if (iq.getTo().getResource() == null)
				iq.getTo().setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
		} else {
			if (!accountManager.exists(iq.getTo().getNode())) {
				throw new ProtocolException(new ItemNotFound(String.format("User named '%s' doesn't exist.", iq.getTo().getNode())));
			}			
		}
		
		if (iq.getType() != Iq.Type.SET) {
			throw new ProtocolException(new BadRequest("Only set type signal allowed."));
		}
		
		if (resourcesService.getResource(iq.getTo()) == null)
			throw new ProtocolException(new RecipientUnavailable("Peer isn't being online now."));
		
		iq.setFrom(sessionJid);
		
		if (isOpenSignal(xep)) {
			processOpenSignal(context, iq, isUserSession, xep);	
		} else if (isOpenedSignal(xep)) {
			processOpenedSignal(context, iq, isUserSession, xep);	
		} else if (isCloseSignal(xep)) {
			processCloseSignal(context, iq, isUserSession, xep);	
		} else if (isClosedSignal(xep)) {
			processClosedSignal(context, iq, isUserSession, xep);	
		} else if (isOfferSignal(xep)) {
			processOfferSignal(context, iq, isUserSession, xep);				
		} else if (isAnswerSignal(xep)) {
			processAnswerSignal(context, iq, isUserSession, xep);							
		} else if (isIceCandidateFoundSignal(xep)) {
			processIceCandidateFoundSignal(context, iq, xep);
		} else {
			throw new ProtocolException(new BadRequest(String.format("Unknown signal ID: %s.", xep.getId())));
		}
	}

	private void processClosedSignal(IProcessingContext context, Iq iq, boolean isUserSession, Signal signal) {
		if (isUserSession)
			throw new ProtocolException(new NotAllowed("Only device can send a closed signal"));
		
		LiveStreamingSession liveStreamingSession = context.removeAttribute(KEY_LIVE_STREAMING_SESSION);
		if (liveStreamingSession != null)
			liveStreamingSession.destroy();
		
		context.write(iq.getTo(), signal);
	}

	private boolean isClosedSignal(Signal signal) {
		return signal.getId().equals(Signal.ID.CLOSED);
	}

	private void processOpenedSignal(IProcessingContext context, Iq iq, boolean isUserSession, Signal signal) {
		if (isUserSession)
			throw new ProtocolException(new NotAllowed("Only device can send a opened signal"));
		
		LiveStreamingSession liveStreamingSession = new LiveStreamingSession(iq.getTo(), iq.getFrom());
		context.setAttribute(KEY_LIVE_STREAMING_SESSION, liveStreamingSession);
		
		context.write(iq.getTo(), signal);
	}

	private boolean isOpenedSignal(Signal signal) {
		return signal.getId().equals(Signal.ID.OPENED);
	}

	private void processIceCandidateFoundSignal(IProcessingContext context, Iq iq, Signal signal) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean isIceCandidateFoundSignal(Signal signal) {
		return signal.getId() == Signal.ID.ICE_CANDIDATE_FOUND;
	}
	
	private void processAnswerSignal(IProcessingContext context, Iq iq, boolean isUserSession, Signal signal) {
		if (isUserSession)
			throw new ProtocolException(new NotAllowed("Only device can send an answer signal"));
		
		LiveStreamingSession liveStreamingSession = context.getAttribute(KEY_LIVE_STREAMING_SESSION);
		if (liveStreamingSession == null)
			throw new IllegalStateException("Null live streaming session.");
		
		// TODO Auto-generated method stub
	}
	
	private void processOfferSignal(IProcessingContext context, Iq iq, boolean isUserSession, Signal signal) {
		if (!isUserSession)
			throw new ProtocolException(new NotAllowed("Only user can send a offer signal"));
		
		LiveStreamingSession liveStreamingSession = context.getAttribute(KEY_LIVE_STREAMING_SESSION);
		if (liveStreamingSession == null)
			throw new IllegalStateException("Null live streaming session.");
		
		if (!liveStreamingSession.createPipeline(kClient)) {
			throw new RuntimeException("Failed to create kurento media pipeline.");
		}
		
		addWebcamListeners(liveStreamingSession);
		sendOfferToWebcam(context, liveStreamingSession);
		
		addWatcherListeners(liveStreamingSession);
		sendAnswerToWatcher(context, signal, liveStreamingSession);
	}

	private void sendAnswerToWatcher(IProcessingContext context, Signal signal,
			LiveStreamingSession liveStreamingSession) {
		String answerToWatcher = liveStreamingSession.getWatcherEndpoint().processOffer(signal.getData());
		Iq answerSignalToWatcher = new Iq(Iq.Type.SET, new Signal(Signal.ID.ANSWER, answerToWatcher));
		
		answerSignalToWatcher.setFrom(liveStreamingSession.getWebcam());
		answerSignalToWatcher.setTo(liveStreamingSession.getWatcher());
		
		context.write(answerSignalToWatcher);
	}

	private void addWatcherListeners(LiveStreamingSession liveStreamingSession) {
		liveStreamingSession.getWatcherEndpoint().addErrorListener(watcherEndpointErrorListener);
		liveStreamingSession.getWatcherEndpoint().addIceCandidateFoundListener(watcherEndpointIceCandidateFoundListener);
	}

	private void addWebcamListeners(LiveStreamingSession liveStreamingSession) {
		liveStreamingSession.getWebcamEndpoint().addErrorListener(webcamEndpointErrorListener);
		liveStreamingSession.getWebcamEndpoint().addIceCandidateFoundListener(webcamEndpointIceCandidateFoundListener);
	}

	private void sendOfferToWebcam(IProcessingContext context, LiveStreamingSession liveStreamingSession) {
		String offerToWebcam = liveStreamingSession.getWebcamEndpoint().generateOffer();	
		Iq offerSignalToWebcam = new Iq(Iq.Type.SET, new Signal(Signal.ID.OFFER, offerToWebcam));
		
		offerSignalToWebcam.setFrom(liveStreamingSession.getWatcher());
		offerSignalToWebcam.setTo(liveStreamingSession.getWebcam());
		
		context.write(offerSignalToWebcam);
	}
	
	private class WebcamEndpointErrorListener implements EventListener<ErrorEvent> {

		@Override
		public void onEvent(ErrorEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class WebcamEndpointIceCandidateFoundListener implements EventListener<IceCandidateFoundEvent> {

		@Override
		public void onEvent(IceCandidateFoundEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class WatcherEndpointErrorListener implements EventListener<ErrorEvent> {

		@Override
		public void onEvent(ErrorEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class WatcherEndpointIceCandidateFoundListener implements EventListener<IceCandidateFoundEvent> {

		@Override
		public void onEvent(IceCandidateFoundEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}

	private boolean isOfferSignal(Signal signal) {
		return signal.getId() == Signal.ID.OFFER;
	}
	
	private boolean isAnswerSignal(Signal signal) {
		return signal.getId() == Signal.ID.ANSWER;
	}

	private void processCloseSignal(IProcessingContext context, Iq iq, boolean isUserSession, Signal signal) {
		if (!isUserSession) {
			throw new ProtocolException(new NotAllowed("Only user can send an close signal."));
		}
		
		context.write(iq);	
	}

	private boolean isCloseSignal(Signal signal) {
		return signal.getId().equals(Signal.ID.CLOSE);
	}

	private void processOpenSignal(IProcessingContext context, Iq iq, boolean isUserSession, Signal signal) {
		if (!isUserSession) {
			throw new ProtocolException(new NotAllowed("Only user can send an open signal."));
		}
		
		context.write(iq);		
	}

	private boolean isOpenSignal(Signal signal) {
		return signal.getId() == Signal.ID.OPEN;
	}
	
	@Override
	public void sessionEstablishing(IConnectionContext context, JabberId sessionJid) throws Exception {
		// NOOP
	}

	@Override
	public void sessionEstablished(IConnectionContext context, JabberId sessionJid) throws Exception {
		// NOOP
	}

	@Override
	public void sessionClosing(IConnectionContext context, JabberId sessionJid) throws Exception {
		// NOOP
	}

	@Override
	public void sessionClosed(IConnectionContext context, JabberId sessionJid) throws Exception {
		LiveStreamingSession liveStreamingSession = context.getAttribute(KEY_LIVE_STREAMING_SESSION);
		if (liveStreamingSession == null)
			return;
		
		sendCloseSignalToWebcam(context, liveStreamingSession);
		liveStreamingSession.destroy();
	}

	private void sendCloseSignalToWebcam(IConnectionContext context, LiveStreamingSession liveStreamingSession) {
		Iq closeSignal = new Iq(Iq.Type.SET, new Signal(Signal.ID.CLOSE));
		closeSignal.setFrom(liveStreamingSession.getWatcher());
		closeSignal.setTo(liveStreamingSession.getWebcam());
		
		context.write(closeSignal);
	}

}
