#include <iostream>
#include <string>

#include "api/audio_codecs/builtin_audio_encoder_factory.h"
#include "api/audio_codecs/builtin_audio_decoder_factory.h"
#include "api/video_codecs/builtin_video_encoder_factory.h"
#include "api/video_codecs/builtin_video_decoder_factory.h"
#include "api/create_peerconnection_factory.h"
#include "modules/video_capture/video_capture_factory.h"

#include "WebcamWebrtcPeer.h"

using namespace std;

WebcamWebrtcPeer::WebcamWebrtcPeer() {
	videoCaptureDeviceName = nullptr;
}

void WebcamWebrtcPeer::open() {}
void WebcamWebrtcPeer::close() {}

rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> WebcamWebrtcPeer::getPeerConnectionFactory() {
	if (!peerConnectionFactory.get())
		createPeerConnectionFactory();

	return peerConnectionFactory;
}

void WebcamWebrtcPeer::createPeerConnectionFactory() {
	if (peerConnectionFactory.get())
		return;

	if (!networkThread.get()) {
		networkThread = rtc::Thread::CreateWithSocketServer();
		networkThread->Start();
	}

	if (!workerThread.get()) {
		workerThread = rtc::Thread::CreateWithSocketServer();
		workerThread->Start();
	}

	if (!signalingThread.get()) {
		signalingThread = rtc::Thread::CreateWithSocketServer();
		signalingThread->Start();
	}

	peerConnectionFactory = webrtc::CreatePeerConnectionFactory(
		networkThread.get() /* network_thread */,
		workerThread.get() /* worker_thread */,
		signalingThread.get(),
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

WebcamWebrtcPeer::~WebcamWebrtcPeer() {
	if (videoCaptureDeviceName) {
		delete videoCaptureDeviceName;
		videoCaptureDeviceName = nullptr;
	}

	if (peerConnectionFactory.get())
		peerConnectionFactory.release();
}

void WebcamWebrtcPeer::OnAddTrack() {}
void WebcamWebrtcPeer::OnSignalingChange(webrtc::PeerConnectionInterface::SignalingState newState) {}
void WebcamWebrtcPeer::OnAddStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream) {}
void WebcamWebrtcPeer::OnRemoveStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream) {}
void WebcamWebrtcPeer::OnDataChannel(rtc::scoped_refptr<webrtc::DataChannelInterface> dataChannel) {}
void WebcamWebrtcPeer::OnRenegotiationNeeded() {}
void WebcamWebrtcPeer::OnIceConnectionChange(
	webrtc::PeerConnectionInterface::IceConnectionState new_state) {}
void WebcamWebrtcPeer::OnStandardizedIceConnectionChange(
	webrtc::PeerConnectionInterface::IceConnectionState new_state) {}
void WebcamWebrtcPeer::OnConnectionChange(
	webrtc::PeerConnectionInterface::PeerConnectionState new_state) {}
void WebcamWebrtcPeer::OnIceGatheringChange(
	webrtc::PeerConnectionInterface::IceGatheringState new_state) {}
void WebcamWebrtcPeer::OnIceCandidate(
	const webrtc::IceCandidateInterface* candidate) {}
void WebcamWebrtcPeer::OnIceCandidateError(const std::string& host_candidate,
	const std::string& url, int error_code, const std::string& error_text) {}
void WebcamWebrtcPeer::OnIceCandidateError(const std::string& address, int port,
	const std::string& url, int error_code, const std::string& error_text) {}
void WebcamWebrtcPeer::OnIceCandidatesRemoved(const std::vector<cricket::Candidate>& candidates) {}
void WebcamWebrtcPeer::OnIceConnectionReceivingChange(bool receiving) {}
void WebcamWebrtcPeer::OnIceSelectedCandidatePairChanged(cricket::CandidatePairChangeEvent& event) {}
void WebcamWebrtcPeer::OnAddTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver,
	const std::vector<rtc::scoped_refptr<webrtc::MediaStreamInterface>>& streams) {}
void WebcamWebrtcPeer::OnTrack(rtc::scoped_refptr<webrtc::RtpTransceiverInterface> transceiver) {}
void WebcamWebrtcPeer::OnRemoveTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver) {}
void WebcamWebrtcPeer::OnInterestingUsage(int usage_pattern) {}
