package com.thefirstlineofcode.sand.server.kurento.webcam;

import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.KurentoClient;
import org.kurento.client.ListenerSubscription;
import org.kurento.client.OfferOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAllowed;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.RecipientUnavailable;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.auth.IAccountManager;
import com.thefirstlineofcode.granite.framework.core.config.IConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.connection.IConnectionContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirer;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirerAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.granite.framework.core.session.ISessionListener;
import com.thefirstlineofcode.granite.framework.core.utils.CommonUtils;
import com.thefirstlineofcode.granite.framework.im.IResourcesService;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class SignalProcessor implements IXepProcessor<Iq, Signal>, ISessionListener,
			IEventFirerAware, IConfigurationAware {
	private static final String CONFIGURATION_KEY_WEB_SERVER_URL = "web.server.url";
	private static final String DEFAULT_WEB_SERVER_URL = "http://files.openvidu.io";
	
	private static final Logger logger = LoggerFactory.getLogger(SignalProcessor.class);
	
	@BeanDependency
	private IAccountManager accountManager;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private KurentoClient kClient;
	
	@BeanDependency
	private IResourcesService resourcesService;
	
	private IEventFirer eventFirer;
	
	private WebcamEndpointErrorListener webcamEndpointErrorListener;
	private WebcamEndpointIceCandidateFoundListener webcamEndpointIceCandidateFoundListener;
	private WatcherEndpointErrorListener watcherEndpointErrorListener;
	private WatcherEndpointIceCandidateFoundListener watcherEndpointIceCandidateFoundListener;
	
	private String webServerUrl;
	
	@Override
	public void process(IProcessingContext context, Iq iq, Signal xep) {
		JabberId sessionJid = context.getJid();
		boolean userSession = isUserSession(sessionJid);
		
		if (iq.getTo() == null)
			CommonUtils.logAndThrow(logger, new ProtocolException(new BadRequest("Null peer JID.")));
		
		if (userSession) {
			if (!deviceManager.deviceNameExists(iq.getTo().getNode())) {
				CommonUtils.logAndThrow(logger, new ProtocolException(new ItemNotFound(String.format("Device named '%s' doesn't exist.", iq.getTo().getNode()))));
			}
			
			if (iq.getTo().getResource() == null)
				iq.getTo().setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
		} else {
			if (!accountManager.exists(iq.getTo().getNode())) {
				CommonUtils.logAndThrow(logger, new ProtocolException(new ItemNotFound(String.format("User named '%s' doesn't exist.", iq.getTo().getNode()))));
			}
		}
		
		if (iq.getType() != Iq.Type.SET) {
			CommonUtils.logAndThrow(logger, new ProtocolException(new BadRequest("Only set type signal allowed.")));
		}
		
		if (resourcesService.getResource(iq.getTo()) == null)
			CommonUtils.logAndThrow(logger, new ProtocolException(new RecipientUnavailable(
					String.format("Peer '%s' isn't being online now.", iq.getTo()))));
		
		iq.setFrom(sessionJid);
		
		if (isOpenSignal(xep)) {
			processOpenSignal(context, iq, userSession, xep);
		} else if (isOpenedSignal(xep)) {
			processOpenedSignal(context, iq, userSession, xep);
		} else if (isCloseSignal(xep)) {
			processCloseSignal(context, iq, userSession, xep);
		} else if (isClosedSignal(xep)) {
			processClosedSignal(context, iq, userSession, xep);
		} else if (isOfferSignal(xep)) {
			processOfferSignal(context, iq, userSession, xep);
		} else if (isAnswerSignal(xep)) {
			processAnswerSignal(context, iq, userSession, xep);
		} else if (isIceCandidateFoundSignal(xep)) {
			processIceCandidateFoundSignal(context, iq, userSession, xep);
		} else if (isErrorSignal(xep)) {
			processErrorSignal(context, iq, userSession, xep);
		} else {
			CommonUtils.logAndThrow(logger, new ProtocolException(new BadRequest(String.format("Unknown signal ID: %s.", xep.getId()))));
		}
	}

	private boolean isUserSession(JabberId sessionJid) {
		boolean isUserSession = false;
		if (accountManager.exists(sessionJid.getNode())) {
			isUserSession = true;
		} else if (deviceManager.deviceNameExists(sessionJid.getNode())) {
			isUserSession = false;
		} else {
			CommonUtils.logAndThrow(logger, new ProtocolException(new NotAllowed(String.format("Neither user nor device. What thing are you?"))));
		}
		return isUserSession;
	}
	
	private void processErrorSignal(IProcessingContext context, Iq iq, boolean userSession, Signal signal) {
		if (userSession)
			CommonUtils.logAndThrow(logger, new ProtocolException(new NotAllowed("Only device can send an error signal")));
		
		// TODO Auto-generated method stub
	}

	private boolean isErrorSignal(Signal signal) {
		return signal.getId().equals(Signal.ID.ERROR);
	}
	
	private void processClosedSignal(IProcessingContext context, Iq iq, boolean userSession, Signal signal) {
		if (userSession)
			CommonUtils.logAndThrow(logger, new ProtocolException(new NotAllowed("Only device can send a closed signal")));
		
		LiveStreamingSession session = LiveStreamingSessions.getInstance().removeSession(iq.getTo());
		if (session != null) {
			removeWebcamListeners(session);
			removeWatcherListeners(session);
			
			session.destroy();
		}
		
		context.write(iq.getTo(), signal);
	}

	private void removeWebcamListeners(LiveStreamingSession session) {
		if (webcamEndpointErrorListener != null) {			
			session.getWebcamEndpoint().removeErrorListener(webcamEndpointErrorListener.getSubscription());
			webcamEndpointErrorListener = null;
		}
		
		if (webcamEndpointIceCandidateFoundListener != null) {			
			session.getWebcamEndpoint().removeIceCandidateFoundListener(webcamEndpointIceCandidateFoundListener.getSubscription());
			webcamEndpointIceCandidateFoundListener = null;
		}
	}

	private void removeWatcherListeners(LiveStreamingSession session) {
		if (watcherEndpointErrorListener != null) {			
			session.getWatcherEndpoint().removeErrorListener(watcherEndpointErrorListener.getSubscription());
			watcherEndpointErrorListener = null;
		}
		
		if (watcherEndpointIceCandidateFoundListener != null) {			
			session.getWatcherEndpoint().removeIceCandidateFoundListener(watcherEndpointIceCandidateFoundListener.getSubscription());
			watcherEndpointIceCandidateFoundListener = null;
		}
	}

	private boolean isClosedSignal(Signal signal) {
		return signal.getId().equals(Signal.ID.CLOSED);
	}

	private void processOpenedSignal(IProcessingContext context, Iq iq, boolean userSession, Signal signal) {
		if (userSession)
			CommonUtils.logAndThrow(logger, new ProtocolException(new NotAllowed("Only device can send a opened signal")));
		
		if (!LiveStreamingSessions.getInstance().createSession(iq.getTo(), iq.getFrom())) {
			CommonUtils.logAndThrow(logger, new RuntimeException("Can't create live streaming session."));
		}
		LiveStreamingSessions.getInstance().getSessionByWatcher(iq.getTo()).setWebServerUrl(webServerUrl);
		
		context.write(iq);
	}

	private boolean isOpenedSignal(Signal signal) {
		return signal.getId().equals(Signal.ID.OPENED);
	}

	private void processIceCandidateFoundSignal(IProcessingContext context, Iq iq,
			boolean userSession, Signal signal) {
		// TODO Auto-generated method stub
		LiveStreamingSession session = null;
		if (userSession) {
			session = LiveStreamingSessions.getInstance().getSessionByWatcher(iq.getFrom());
		} else {
			session = LiveStreamingSessions.getInstance().getSessionByWebcam(iq.getFrom());			
		}
		
		if (session == null)
			throw new IllegalStateException("Null live streaming session.");
		
		IceCandidate iceCandidate = null;
		try {
			iceCandidate = getIceCandidate(signal.getData());
		} catch (JsonSyntaxException e) {
			CommonUtils.logAndThrow(logger, e);
		}
		
		if (userSession) {
			session.getWatcherEndpoint().addIceCandidate(iceCandidate);
		} else {
			session.getWebcamEndpoint().addIceCandidate(iceCandidate);
		}
	}
	
	private IceCandidate getIceCandidate(String jsonCandidate) {
		Gson gson = new Gson();
		IceCandidateObject iceCandidateObj = gson.fromJson(jsonCandidate, IceCandidateObject.class);
		
		if (iceCandidateObj.getSdpMid() == null ||
				iceCandidateObj.getSdpMLineIndex() == -1 ||
				iceCandidateObj.getCandidate() == null)
			CommonUtils.logAndThrow(logger, new ProtocolException(new BadRequest(String.format(
					"Malformed ICE candidate data. Data: %s.", jsonCandidate))));
		
		return new IceCandidate(iceCandidateObj.getCandidate(), iceCandidateObj.getSdpMid(), iceCandidateObj.getSdpMLineIndex());
	}
	
	public class IceCandidateObject {
		private String candidate;
		private String sdpMid;
		private int sdpMLineIndex;
		
		public IceCandidateObject() {
			this(null, null, -1);
		}
		
		public IceCandidateObject(String candidate, String sdpMid, int sdpMLineIndex) {
			this.candidate = candidate;
			this.sdpMid = sdpMid;
			this.sdpMLineIndex = sdpMLineIndex;
		}
		
		public String getCandidate() {
			return candidate;
		}
		
		public void setCandidate(String candidate) {
			this.candidate = candidate;
		}
		
		public String getSdpMid() {
			return sdpMid;
		}
		
		public void setSdpMid(String sdpMid) {
			this.sdpMid = sdpMid;
		}
		
		public int getSdpMLineIndex() {
			return sdpMLineIndex;
		}
		
		public void setSdpMLineIndex(int sdpMLineIndex) {
			this.sdpMLineIndex = sdpMLineIndex;
		}
	}

	private boolean isIceCandidateFoundSignal(Signal signal) {
		return signal.getId() == Signal.ID.ICE_CANDIDATE_FOUND;
	}
	
	private void processAnswerSignal(IProcessingContext context, Iq iq, boolean userSession, Signal signal) {
		if (userSession)
			CommonUtils.logAndThrow(logger, new ProtocolException(new NotAllowed("Only device can send an answer signal")));
		
		LiveStreamingSession session = LiveStreamingSessions.getInstance().getSessionByWebcam(iq.getFrom());
		if (session == null)
			CommonUtils.logAndThrow(logger, new IllegalStateException("Null live streaming session."));
		
		session.getWebcamEndpoint().processAnswer(signal.getData());
		session.getWebcamEndpoint().gatherCandidates();
	}
	
	private void processOfferSignal(IProcessingContext context, Iq iq, boolean userSession, Signal signal) {
		if (!userSession)
			CommonUtils.logAndThrow(logger, new ProtocolException(new NotAllowed("Only user can send a offer signal")));
		
		LiveStreamingSession session = LiveStreamingSessions.getInstance().getSessionByWatcher(iq.getFrom());
		if (session == null)
			CommonUtils.logAndThrow(logger, new IllegalStateException("Null live streaming session."));
		
		if (!session.createPipeline(kClient)) {
			CommonUtils.logAndThrow(logger, new RuntimeException("Failed to create kurento media pipeline."));
		}
		
		addWebcamListeners(session);
		sendOfferToWebcam(context, session);
		
		addWatcherListeners(session);
		sendAnswerToWatcher(context, signal, session);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				session.getWatcherEndpoint().gatherCandidates();
			}
		}).start();
	}

	private void sendAnswerToWatcher(IProcessingContext context, Signal signal, LiveStreamingSession session) {
		String answerToWatcher = session.getWatcherEndpoint().processOffer(signal.getData());
		Iq answerSignalToWatcher = new Iq(Iq.Type.SET, new Signal(Signal.ID.ANSWER, answerToWatcher));
		
		answerSignalToWatcher.setFrom(session.getWebcam());
		answerSignalToWatcher.setTo(session.getWatcher());
		
		context.write(answerSignalToWatcher);
	}

	private void addWatcherListeners(LiveStreamingSession session) {
		watcherEndpointErrorListener = new WatcherEndpointErrorListener(session.getWatcher(), session.getWebcam());
		ListenerSubscription subscription = session.getWatcherEndpoint().addErrorListener(watcherEndpointErrorListener);
		watcherEndpointErrorListener.setSubscription(subscription);
		
		watcherEndpointIceCandidateFoundListener = new WatcherEndpointIceCandidateFoundListener(session.getWatcher(), session.getWebcam());
		subscription = session.getWatcherEndpoint().addIceCandidateFoundListener(watcherEndpointIceCandidateFoundListener);
		watcherEndpointIceCandidateFoundListener.setSubscription(subscription);
	}

	private void addWebcamListeners(LiveStreamingSession session) {
		webcamEndpointErrorListener = new WebcamEndpointErrorListener(session.getWatcher(), session.getWebcam());
		ListenerSubscription subscription = session.getWebcamEndpoint().addErrorListener(webcamEndpointErrorListener);
		webcamEndpointErrorListener.setSubscription(subscription);
		
		webcamEndpointIceCandidateFoundListener = new WebcamEndpointIceCandidateFoundListener(session.getWatcher(), session.getWebcam());
		subscription = session.getWebcamEndpoint().addIceCandidateFoundListener(webcamEndpointIceCandidateFoundListener);
		webcamEndpointIceCandidateFoundListener.setSubscription(subscription);
	}

	private void sendOfferToWebcam(IProcessingContext context, LiveStreamingSession session) {
		String offerToWebcam = session.getWebcamEndpoint().generateOffer(createOfferOptions());	
		Iq offerSignalToWebcam = new Iq(Iq.Type.SET, new Signal(Signal.ID.OFFER, offerToWebcam));
		
		offerSignalToWebcam.setFrom(session.getWatcher());
		offerSignalToWebcam.setTo(session.getWebcam());
		
		context.write(offerSignalToWebcam);
	}

	private OfferOptions createOfferOptions() {
		OfferOptions options = new OfferOptions();
		options.setOfferToReceiveAudio(false);
		options.setOfferToReceiveVideo(true);
		
		return options;
	}
	
	private class WebcamEndpointErrorListener extends KurentoEventListener implements EventListener<ErrorEvent> {
		public WebcamEndpointErrorListener(JabberId watcher, JabberId webcam) {
			super(watcher, webcam);
		}

		@Override
		public void onEvent(ErrorEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class WebcamEndpointIceCandidateFoundListener extends KurentoEventListener implements EventListener<IceCandidateFoundEvent> {
		public WebcamEndpointIceCandidateFoundListener(JabberId watcher, JabberId webcam) {
			super(watcher, webcam);
		}
		
		@Override
		public void onEvent(IceCandidateFoundEvent event) {
			Gson gson = new Gson();
			String jsonCandidate = gson.toJson(event.getCandidate());
			
			eventFirer.fire(new KurentoIceCandidateFoundEvent(watcher, webcam, jsonCandidate));
		}
		
	}
	
	private class KurentoEventListener {
		protected JabberId watcher;
		protected JabberId webcam;
		protected ListenerSubscription subscription;
		
		public KurentoEventListener(JabberId watcher, JabberId webcam) {
			this.watcher = watcher;
			this.webcam = webcam;
		}
		
		public ListenerSubscription getSubscription() {
			return subscription;
		}

		public void setSubscription(ListenerSubscription subscription) {
			this.subscription = subscription;
		}
	}
	
	private class WatcherEndpointErrorListener extends KurentoEventListener implements EventListener<ErrorEvent> {
		public WatcherEndpointErrorListener(JabberId watcher, JabberId webcam) {
			super(watcher, webcam);
		}

		@Override
		public void onEvent(ErrorEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class WatcherEndpointIceCandidateFoundListener extends KurentoEventListener implements EventListener<IceCandidateFoundEvent> {
		public WatcherEndpointIceCandidateFoundListener(JabberId watcher, JabberId webcam) {
			super(watcher, webcam);
		}

		@Override
		public void onEvent(IceCandidateFoundEvent event) {
			Gson gson = new Gson();
			String jsonCandidate = gson.toJson(event.getCandidate());
			
			eventFirer.fire(new KurentoIceCandidateFoundEvent(webcam, watcher, jsonCandidate));
		}
		
	}

	private boolean isOfferSignal(Signal signal) {
		return signal.getId() == Signal.ID.OFFER;
	}
	
	private boolean isAnswerSignal(Signal signal) {
		return signal.getId() == Signal.ID.ANSWER;
	}

	private void processCloseSignal(IProcessingContext context, Iq iq, boolean userSession, Signal signal) {
		if (!userSession) {
			throw new ProtocolException(new NotAllowed("Only user can send an close signal."));
		}
		
		LiveStreamingSession session = LiveStreamingSessions.getInstance().removeSession(iq.getFrom());
		if (session != null) {
			session.destroy();
		}
		
		context.write(iq);	
	}

	private boolean isCloseSignal(Signal signal) {
		return signal.getId().equals(Signal.ID.CLOSE);
	}

	private void processOpenSignal(IProcessingContext context, Iq iq, boolean userSession, Signal signal) {
		if (!userSession) {
			CommonUtils.logAndThrow(logger, new ProtocolException(new NotAllowed("Only user can send an open signal.")));
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
		boolean userSession = isUserSession(sessionJid);
		LiveStreamingSession session;
		if (userSession)
			session = LiveStreamingSessions.getInstance().getSessionByWatcher(sessionJid);
		else
			session = LiveStreamingSessions.getInstance().getSessionByWebcam(sessionJid);
		
		if (session == null)
			return;
		
		removeWebcamListeners(session);
		removeWatcherListeners(session);
		
		if (userSession)
			sendCloseSignalToWebcam(context, session.getWatcher(), session.getWebcam());
		
		session.destroy();
		
	}

	private void sendCloseSignalToWebcam(IConnectionContext context, JabberId watcher, JabberId webcam) {
		Iq closeSignal = new Iq(Iq.Type.SET, new Signal(Signal.ID.CLOSE));
		closeSignal.setFrom(watcher);
		closeSignal.setTo(webcam);
		
		context.write(closeSignal);
	}

	@Override
	public void setEventFirer(IEventFirer eventFirer) {
		this.eventFirer = eventFirer;
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		webServerUrl = configuration.getString(CONFIGURATION_KEY_WEB_SERVER_URL, DEFAULT_WEB_SERVER_URL);
	}

}
