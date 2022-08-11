#include <iostream>
#include <string>

#include "api/audio_codecs/builtin_audio_encoder_factory.h"
#include "api/audio_codecs/builtin_audio_decoder_factory.h"
#include "api/video_codecs/builtin_video_encoder_factory.h"
#include "api/video_codecs/builtin_video_decoder_factory.h"
#include "api/create_peerconnection_factory.h"
#include "absl/memory/memory.h"

#include "WebcamWebrtcPeer.h"

using namespace std;

class DummySetSessionDescriptionObserver : public webrtc::SetSessionDescriptionObserver {
public:
	static rtc::scoped_refptr<DummySetSessionDescriptionObserver> Create() {
		return new rtc::RefCountedObject<DummySetSessionDescriptionObserver>();
	}

	virtual void OnSuccess() {
		cout << "Session description is set." << endl;
	}

	virtual void OnFailure(webrtc::RTCError error) {
		cout << "Set session descriptin failed. Error type: "<< ToString(error.type()) <<
			"Error message:" << error.message() << "." << endl;
	}
};

class AnswerObserver : public webrtc::CreateSessionDescriptionObserver {
public:
	AnswerObserver(cppnet::Handle handle, rtc::scoped_refptr<webrtc::PeerConnectionInterface> peerConnection) {
		this->handle = handle;
		this->peerConnection = peerConnection;
	}

	void OnSuccess(webrtc::SessionDescriptionInterface* sessionDescription) {
		peerConnection->SetLocalDescription(DummySetSessionDescriptionObserver::Create(), sessionDescription);

		std::string sdp;
		sessionDescription->ToString(&sdp);
	}

	void OnFailure(webrtc::RTCError error) {
		cout << "Failed to create answer. Error message: " << error.message() << "." << endl;
	}

private:
	cppnet::Handle handle;
	rtc::scoped_refptr<webrtc::PeerConnectionInterface> peerConnection;
};

WebcamWebrtcPeer::WebcamWebrtcPeer() {
	videoCaptureDeviceName = nullptr;
	opened = false;
}

void WebcamWebrtcPeer::open() {
	if (!peerConnection.get()) {
		createPeerConnection();
		addVideoTrack();
	}

	opened = true;
}

void WebcamWebrtcPeer::close() {
	stopVideoRenderer();

	if (peerConnection.get()) {
		peerConnection->Close();
		peerConnection.release();
	}

	if (peerConnectionFactory.get()) {
		peerConnectionFactory.release();
	}

	opened = false;
}

bool WebcamWebrtcPeer::isOpened() {
	return opened;
}

bool WebcamWebrtcPeer::isClosed() {
	return !opened;
}

void WebcamWebrtcPeer::createPeerConnection() {
	if (peerConnection.get())
		return;

	if (!peerConnectionFactory.get())
		createPeerConnectionFactory();

	webrtc::PeerConnectionInterface::RTCConfiguration configutration;
	configutration.sdp_semantics = webrtc::SdpSemantics::kUnifiedPlan;
	configutration.enable_dtls_srtp = true;

	webrtc::PeerConnectionDependencies dependencies(this);

	peerConnection = peerConnectionFactory->CreatePeerConnection(configutration, std::move(dependencies));
}

void WebcamWebrtcPeer::addVideoTrack() {
	if (!peerConnection->GetSenders().empty()) {
		return;  // Track has already been added.
	}

	// TODO
	if (!videoDevice || !videoDevice.get()) {
		videoDevice = CapturerTrackSource::Create();

		if (!videoDevice) {
			// TODO Send error to native service client
			return;
		}
	}
	
	if(!videoTrack || !videoTrack.get()) {
		videoTrack = peerConnectionFactory->CreateVideoTrack(labelVideoTrack, videoDevice);

		if(!videoTrack) {
			// TODO Send error to native service client
			return;
		}
	}

	// startVideoRenderer(videoTrack);

	auto resultOrError =
		peerConnection->AddTrack(videoTrack, {labelVideoStream});

	if (!resultOrError.ok()) {
		// TODO Send error to native service client
		return;
	}
}

void WebcamWebrtcPeer::startVideoRenderer(webrtc::VideoTrackInterface *videoTrack) {
	localRenderer.reset(new VideoRenderer(videoTrack));
}

void WebcamWebrtcPeer::stopVideoRenderer() {
	localRenderer.reset();
}

void WebcamWebrtcPeer::createPeerConnectionFactory() {
	if (peerConnectionFactory.get())
		return;

	peerConnectionFactory = webrtc::CreatePeerConnectionFactory(
		nullptr /* network_thread */,
		nullptr /* worker_thread */,
		nullptr,
		nullptr /* default_adm */,
		webrtc::CreateBuiltinAudioEncoderFactory(),
		webrtc::CreateBuiltinAudioDecoderFactory(),
		webrtc::CreateBuiltinVideoEncoderFactory(),
		webrtc::CreateBuiltinVideoDecoderFactory(),
		nullptr /* audio_mixer */,
		nullptr /* audio_processing */
	);
}

const char * WebcamWebrtcPeer::getVideoCaptureDeviceName() {
	if (videoCaptureDeviceName)
		return videoCaptureDeviceName;

	webrtc::VideoCaptureModule::DeviceInfo *deviceInfo = webrtc::VideoCaptureFactory::CreateDeviceInfo();
	if (deviceInfo && deviceInfo->NumberOfDevices() > 0) {
		videoCaptureDeviceName = (char *)malloc(sizeof(char) * 256);
		deviceInfo->GetDeviceName(0, videoCaptureDeviceName, 256, 0, 0);
	}

	if (deviceInfo)
		delete deviceInfo;


	return videoCaptureDeviceName;
}

void WebcamWebrtcPeer::offered(cppnet::Handle handle, std::string offerSdp) {
	if (!opened) {
		static const char notOpenedError[] = "ERROR Not opened.";
		handle->Write(notOpenedError, sizeof(notOpenedError));

		return;
	}

	webrtc::SdpType offerType = webrtc::SdpType::kOffer;
	webrtc::SdpParseError error;
	std::unique_ptr<webrtc::SessionDescriptionInterface> sessionDescription =
		webrtc::CreateSessionDescription(offerType, offerSdp, &error);
	if (!sessionDescription) {
		static const char offerNotParsed[] = "ERROR Can't parse offer SDP.";
		cout << "Can't parse offer SDP. Error: " << error.description << endl;
		handle->Write(offerNotParsed, sizeof(offerNotParsed));

		return;
	}

	peerConnection->SetRemoteDescription(
		DummySetSessionDescriptionObserver::Create().get(),
		sessionDescription.release());

	rtc::scoped_refptr<AnswerObserver> answerObserver =
		new rtc::RefCountedObject<AnswerObserver>(handle, peerConnection);
	peerConnection->CreateAnswer(answerObserver, webrtc::PeerConnectionInterface::RTCOfferAnswerOptions());
}

WebcamWebrtcPeer::~WebcamWebrtcPeer() {
	if (videoCaptureDeviceName) {
		delete videoCaptureDeviceName;
		videoCaptureDeviceName = nullptr;
	}
}

void WebcamWebrtcPeer::OnSignalingChange(webrtc::PeerConnectionInterface::SignalingState newState) {
	cout << "Signaling changed. new Signaling state: " << newState << "." << endl;
}

void WebcamWebrtcPeer::OnAddStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream) {}
void WebcamWebrtcPeer::OnRemoveStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream) {}
void WebcamWebrtcPeer::OnDataChannel(rtc::scoped_refptr<webrtc::DataChannelInterface> dataChannel) {
	cout << "Data channel is ready." << endl;
}

void WebcamWebrtcPeer::OnRenegotiationNeeded() {}
void WebcamWebrtcPeer::OnIceConnectionChange(
		webrtc::PeerConnectionInterface::IceConnectionState new_state) {
	cout << "ICE connection changed. New state: " << new_state << "." << endl;
}

void WebcamWebrtcPeer::OnStandardizedIceConnectionChange(
		webrtc::PeerConnectionInterface::IceConnectionState new_state) {
	cout << "Standardized ICE connection changed. New state: " << new_state << "." << endl;	
}

void WebcamWebrtcPeer::OnConnectionChange(
		webrtc::PeerConnectionInterface::PeerConnectionState new_state) {
	// cout << "Connection changed. New state: " << new_state << "." << endl;
	cout << "Connection changed." << endl;
}

void WebcamWebrtcPeer::OnIceGatheringChange(
		webrtc::PeerConnectionInterface::IceGatheringState new_state) {
	cout << "ICE gathering changed. New state: " << new_state << endl;
}

void WebcamWebrtcPeer::OnIceCandidate(
		const webrtc::IceCandidateInterface* candidate) {
	cout << "New candidate found. SDP mid of new candidate: " << candidate->sdp_mid() << endl;
}
void WebcamWebrtcPeer::OnIceCandidateError(const std::string& host_candidate,
		const std::string& url, int error_code, const std::string& error_text) {
	cout << "ICE candidate error occurred. Error text: " << error_text << endl;
}

void WebcamWebrtcPeer::OnIceCandidateError(const std::string& address, int port,
		const std::string& url, int error_code, const std::string& error_text) {
	cout << "ICE candidate error occurred. Error text: " << error_text << endl;
}

void WebcamWebrtcPeer::OnIceCandidatesRemoved(const std::vector<cricket::Candidate>& candidates) {}
void WebcamWebrtcPeer::OnIceConnectionReceivingChange(bool receiving) {}
void WebcamWebrtcPeer::OnIceSelectedCandidatePairChanged(cricket::CandidatePairChangeEvent& event) {
	cout << "ICE selected candidate pair changed. Local address of new candidate pair: " <<
		event.selected_candidate_pair.local_candidate().address().ToString() <<
		". Local address of new candidate pair: " <<
		event.selected_candidate_pair.remote_candidate().address().ToString() << "." << endl;
}
void WebcamWebrtcPeer::OnAddTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver,
		const std::vector<rtc::scoped_refptr<webrtc::MediaStreamInterface>>& streams) {
	cout << "Peer track added. Size of streams: " << streams.size() << "." << endl;
}
void WebcamWebrtcPeer::OnTrack(rtc::scoped_refptr<webrtc::RtpTransceiverInterface> transceiver) {
	cout << "Track added. Media type: " << transceiver->media_type() << "." << endl;
}
void WebcamWebrtcPeer::OnRemoveTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver) {}
void WebcamWebrtcPeer::OnInterestingUsage(int usage_pattern) {}
