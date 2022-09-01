package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Context;
import android.util.JsonWriter;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.client.webcam.AbstractWatcher;
import com.thefirstlineofcode.sand.client.webcam.IWebrtcPeer;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webrtc.CandidatePairChangeEvent;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebcamWatcher extends AbstractWatcher implements IWebrtcPeer.Listener {
	private static final String FIELD_TRIALS = PeerConnectionFactory.TRIAL_ENABLED;
	private static final String NAME_ICE_CANDIDATE_SDP_MID = "sdpMid";
	private static final String NAME_ICE_CANDIDATE_SDP_M_LINE_INDEX = "sdpMLineIndex";
	private static final String NAME_ICE_CANDIDATE_SDP = "candidate";
	
	private static final Logger logger = LoggerFactory.getLogger(WebcamWatcher.class);
	
	protected List<Listener> watchListeners;
	protected ExecutorService executorService;
	protected Context appContext;
	protected PeerConnectionFactory peerConnectionFactory;
	protected PeerConnection peerConnection;
	protected PeerConnectionObserver peerConnectionObserver;
	protected MediaConstraints sdpMediaConstraints;
	protected SessionDescription localSessionDescription;
	protected SurfaceViewRenderer videoRenderer;
	private EglBase eglBase;
	
	private interface Listener {
		void occurred(WatchException e);
	}
	
	public WebcamWatcher(IChatServices chatServices, Context appContext,
						 JabberId peer, SurfaceViewRenderer videoRenderer) {
		super(chatServices, peer);
		
		this.appContext = appContext;
		this.videoRenderer = videoRenderer;
		
		executorService = Executors.newSingleThreadExecutor();
		watchListeners = new ArrayList<>();
		
		PeerConnectionFactory.initialize(createInitializationOptions());
	}
	
	@Override
	public void watch() {
		if (!isStarted())
			start();
		
		if (eglBase == null)
			eglBase = EglBase.create();
		
		if (peerConnection == null)
			createPeerConnection();
		
		addListener(this);
		
		if (!opened) {
			processSignal(Signal.ID.OPEN);
		} else {
			opened();
		}
	}
	
	@Override
	public void stop() {
		if (opened)
			close();
		
		eglBase.release();
		eglBase = null;
		
		super.stop();
	}
	
	@Override
	public void opened() {
		super.opened();
		
		offer();
	}
	
	protected void offer() {
		if (sdpMediaConstraints == null)
			sdpMediaConstraints = createSdpMediaConstraints();
		
		peerConnection.createOffer(new CreateOfferObserver(), sdpMediaConstraints);
	}
	
	private class CreateOfferObserver implements SdpObserver {
		
		@Override
		public void onCreateSuccess(SessionDescription sessionDescription) {
			localSessionDescription = sessionDescription;
			processSignal(Signal.ID.OFFER, sessionDescription.description);
		}
		
		@Override
		public void onSetSuccess() {}
		
		@Override
		public void onCreateFailure(String s) {
			notifyExceptionToListeners(new WatchException(
					String.format("Failed to create offer SDP. Error message: %s", s)));
		}
		
		@Override
		public void onSetFailure(String s) {}
	}
	
	private class SetLocalSessionDescriptionObserver implements SdpObserver {
		private String answerSdp;
		
		public SetLocalSessionDescriptionObserver(String answerSdp) {
			this.answerSdp = answerSdp;
		}
		
		@Override
		public void onCreateSuccess(SessionDescription sessionDescription) {}
		
		@Override
		public void onSetSuccess() {
			peerConnection.setRemoteDescription(new SetRemoteSessionDescriptionObserver(),
					new SessionDescription(SessionDescription.Type.ANSWER, answerSdp));
		}
		
		@Override
		public void onCreateFailure(String s) {}
		
		@Override
		public void onSetFailure(String s) {
			notifyExceptionToListeners(new WatchException(
					String.format("Failed to set local session description. Error message: %s", s)));
		}
	}
	
	private class SetRemoteSessionDescriptionObserver implements SdpObserver {
		@Override
		public void onCreateSuccess(SessionDescription sessionDescription) {}
		
		@Override
		public void onSetSuccess() {}
		
		@Override
		public void onCreateFailure(String s) {}
		
		@Override
		public void onSetFailure(String s) {
			notifyExceptionToListeners(new WatchException(
					String.format("Failed to set remote session description. Error message: %s", s)));
		}
	}
	
	private void notifyExceptionToListeners(WatchException e) {
		for (Listener listener : watchListeners) {
			listener.occurred(e);
		}
	}
	
	protected MediaConstraints createSdpMediaConstraints() {
		MediaConstraints sdpMediaConstraints = new MediaConstraints();
		sdpMediaConstraints.mandatory.add(
				new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
		sdpMediaConstraints.mandatory.add(
				new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"));
		
		return sdpMediaConstraints;
	}
	
	protected void createPeerConnection() {
		if (peerConnection != null)
			return;
		
		if (peerConnectionFactory == null)
			createPeerConnectionFactory();
		
		PeerConnection.RTCConfiguration rtcConfiguration =
				new PeerConnection.RTCConfiguration(MainApplication.ICE_SERVERS);
		rtcConfiguration.disableIpv6 = true;
		rtcConfiguration.disableIPv6OnWifi = true;
		rtcConfiguration.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
		
		peerConnectionObserver = new PeerConnectionObserver();
		
		peerConnection = peerConnectionFactory.createPeerConnection(
				rtcConfiguration, peerConnectionObserver);
		
		peerConnection.addTransceiver(MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
				new RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY));
	}
	
	protected void createPeerConnectionFactory() {
		if (peerConnectionFactory != null)
			return;
		
		VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
				eglBase.getEglBaseContext(), true, false);
		VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(
				eglBase.getEglBaseContext());
		peerConnectionFactory = PeerConnectionFactory.builder().
				setOptions(new PeerConnectionFactory.Options()).
				setVideoEncoderFactory(encoderFactory).
				setVideoDecoderFactory(decoderFactory).
				createPeerConnectionFactory();
	}
	
	private PeerConnectionFactory.InitializationOptions createInitializationOptions() {
		PeerConnectionFactory.InitializationOptions initializationOptions =
				PeerConnectionFactory.InitializationOptions.builder(appContext).
				setFieldTrials(FIELD_TRIALS).
				setEnableInternalTracer(true).
				createInitializationOptions();
		
		return initializationOptions;
	}
	
	@Override
	public void close() {
		processSignal(Signal.ID.CLOSE);
		
		if (peerConnection != null) {
			peerConnection.dispose();
			peerConnection = null;
			
			peerConnectionObserver = null;
		}
		
		if (peerConnectionFactory != null) {
			peerConnectionFactory.dispose();
			peerConnectionFactory = null;
		}
		
		videoRenderer.release();
	}
	
	@Override
	public void offered(String offerSdp) {
		throw new IllegalStateException("Did a watcher receive a offer SDP?");
	}
	
	@Override
	public void answered(String answerSdp) {
		peerConnection.setLocalDescription(new SetLocalSessionDescriptionObserver(answerSdp),
				localSessionDescription);
	}
	
	@Override
	public void iceCandidateFound(String candidate) {
		try {
			JSONObject reader = new JSONObject(candidate);
			String sdpMid = reader.getString(NAME_ICE_CANDIDATE_SDP_MID);
			int sdpMLineIndex = reader.getInt(NAME_ICE_CANDIDATE_SDP_M_LINE_INDEX);
			String sdp = reader.getString(NAME_ICE_CANDIDATE_SDP);
			
			boolean successful = peerConnection.addIceCandidate(new IceCandidate(
					sdpMid, sdpMLineIndex, sdp));
			if (!successful) {
				logger.error(String.format("Can't add ICE candidate. Candidate: %s.", candidate));
			}
		} catch (JSONException e) {
			notifyExceptionToListeners(new WatchException("Failed to parse ICE candidate.", e));
		}
	}
	
	@Override
	protected void processPeerSignal(Signal.ID id, String data) {
		if (id != Signal.ID.OPENED &&
				id != Signal.ID.CLOSED &&
				id != Signal.ID.ANSWER &&
				id != Signal.ID.ICE_CANDIDATE_FOUND) {
			throw new RuntimeException(String.format("The signal '%s' shouldn't occurred on watcher.", id));
		}
		
		super.processPeerSignal(id, data);
	}
	
	private class PeerConnectionObserver implements PeerConnection.Observer {
		@Override
		public void onSignalingChange(PeerConnection.SignalingState signalingState) {
			logger.info("Signaling state changed. Current state: {}.", signalingState);
		}
		
		@Override
		public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
			logger.info("ICE connection state changed. Current state: {}.", iceConnectionState);
		}
		
		@Override
		public void onStandardizedIceConnectionChange(PeerConnection.IceConnectionState newState) {
			PeerConnection.Observer.super.onStandardizedIceConnectionChange(newState);
		}
		
		@Override
		public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
			logger.info("Connection state changed. Current state: {}.", newState);
		}
		
		@Override
		public void onIceConnectionReceivingChange(boolean b) {}
		
		@Override
		public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
			logger.info("ICE gathering state changed. Current state: {}.", iceGatheringState);
		}
		
		@Override
		public void onIceCandidate(IceCandidate iceCandidate) {
			logger.info("A local ICE candidate found. Candidate SDP: {}.", iceCandidate.sdp);
			
			StringWriter out = new StringWriter();
			JsonWriter writer = new JsonWriter(out);
			try {
				writer.beginObject();
				writer.name(NAME_ICE_CANDIDATE_SDP_MID).value(iceCandidate.sdpMid).
				name(NAME_ICE_CANDIDATE_SDP_M_LINE_INDEX).value(iceCandidate.sdpMLineIndex).
						name(NAME_ICE_CANDIDATE_SDP).value(iceCandidate.sdp);
				writer.endObject();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			processSignal(Signal.ID.ICE_CANDIDATE_FOUND, out.toString());
		}
		
		@Override
		public void onIceCandidatesRemoved(IceCandidate[] IceCandidates) {
			for (IceCandidate iceCandidate : IceCandidates) {
				logger.info("ICE candidate removed. Candidate SDP: {}.", iceCandidate.sdp);
			}
		}
		
		@Override
		public void onSelectedCandidatePairChanged(CandidatePairChangeEvent event) {
			PeerConnection.Observer.super.onSelectedCandidatePairChanged(event);
			
			logger.info("Selected candidate pair changed. Current local candidate SDP: {}. Current remote candidate SDP: {}.",
					event.local.sdp, event.remote.sdp);
		}
		
		@Override
		public void onAddStream(MediaStream mediaStream) {}
		
		@Override
		public void onRemoveStream(MediaStream mediaStream) {}
		
		@Override
		public void onDataChannel(DataChannel dataChannel) {}
		
		@Override
		public void onRenegotiationNeeded() {}
		
		@Override
		public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
			logger.info("onAddTrack.");
		}
		
		@Override
		public void onTrack(RtpTransceiver transceiver) {
			PeerConnection.Observer.super.onTrack(transceiver);
		}
	}
	
	public void addWatchListener(Listener listener) {
		if (!watchListeners.contains(listener))
			watchListeners.add(listener);
	}
	
	public boolean removeWatchListener(Listener listener) {
		return watchListeners.remove(listener);
	}
}
