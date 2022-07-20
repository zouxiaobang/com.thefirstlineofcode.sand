#include <iostream>
#include <string>

#include "api/audio_codecs/builtin_audio_encoder_factory.h"
#include "api/audio_codecs/builtin_audio_decoder_factory.h"
#include "api/video_codecs/builtin_video_encoder_factory.h"
#include "api/video_codecs/builtin_video_decoder_factory.h"
#include "api/create_peerconnection_factory.h"
#include "modules/video_capture/video_capture_factory.h"

#include "CameraRtcSourcePeer.h"

using namespace std;

CameraRtcSourcePeer::CameraRtcSourcePeer() {
	videoCaptureDeviceName = nullptr;
}

void CameraRtcSourcePeer::open() {}
void CameraRtcSourcePeer::close() {}

rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> CameraRtcSourcePeer::getPeerConnectionFactory() {
	if (!peerConnectionFactory.get())
		createPeerConnectionFactory();

	return peerConnectionFactory;
}

void CameraRtcSourcePeer::createPeerConnectionFactory() {
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

char *CameraRtcSourcePeer::getVideoCaptureDeviceName() {
	if (videoCaptureDeviceName)
		return videoCaptureDeviceName;

	webrtc::VideoCaptureModule::DeviceInfo* deviceInfo = webrtc::VideoCaptureFactory::CreateDeviceInfo();
	if (deviceInfo && deviceInfo->NumberOfDevices() > 0) {
		videoCaptureDeviceName = (char*)malloc(sizeof(char) * 256);
		deviceInfo->GetDeviceName(0, videoCaptureDeviceName, 256, 0, 0);
	}

	if (deviceInfo)
		delete deviceInfo;
}

CameraRtcSourcePeer::~CameraRtcSourcePeer() {
	if (videoCaptureDeviceName) {
		delete videoCaptureDeviceName;
		videoCaptureDeviceName = nullptr;
	}

	if (peerConnectionFactory.get())
		peerConnectionFactory.release();
}

void CameraRtcSourcePeer::OnAddTrack() {}
void CameraRtcSourcePeer::OnSignalingChange(webrtc::PeerConnectionInterface::SignalingState newState) {}
void CameraRtcSourcePeer::OnAddStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream) {}
void CameraRtcSourcePeer::OnRemoveStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream) {}
void CameraRtcSourcePeer::OnDataChannel(rtc::scoped_refptr<webrtc::DataChannelInterface> dataChannel) {}
void CameraRtcSourcePeer::OnRenegotiationNeeded() {}
void CameraRtcSourcePeer::OnIceConnectionChange(
	webrtc::PeerConnectionInterface::IceConnectionState new_state) {}
void CameraRtcSourcePeer::OnStandardizedIceConnectionChange(
	webrtc::PeerConnectionInterface::IceConnectionState new_state) {}
void CameraRtcSourcePeer::OnConnectionChange(
	webrtc::PeerConnectionInterface::PeerConnectionState new_state) {}
void CameraRtcSourcePeer::OnIceGatheringChange(
	webrtc::PeerConnectionInterface::IceGatheringState new_state) {}
void CameraRtcSourcePeer::OnIceCandidate(
	const webrtc::IceCandidateInterface* candidate) {}
void CameraRtcSourcePeer::OnIceCandidateError(const std::string& host_candidate,
	const std::string& url, int error_code, const std::string& error_text) {}
void CameraRtcSourcePeer::OnIceCandidateError(const std::string& address, int port,
	const std::string& url, int error_code, const std::string& error_text) {}
void CameraRtcSourcePeer::OnIceCandidatesRemoved(const std::vector<cricket::Candidate>& candidates) {}
void CameraRtcSourcePeer::OnIceConnectionReceivingChange(bool receiving) {}
void CameraRtcSourcePeer::OnIceSelectedCandidatePairChanged(cricket::CandidatePairChangeEvent& event) {}
void CameraRtcSourcePeer::OnAddTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver,
	const std::vector<rtc::scoped_refptr<webrtc::MediaStreamInterface>>& streams) {}
void CameraRtcSourcePeer::OnTrack(rtc::scoped_refptr<webrtc::RtpTransceiverInterface> transceiver) {}
void CameraRtcSourcePeer::OnRemoveTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver) {}
void CameraRtcSourcePeer::OnInterestingUsage(int usage_pattern) {}
