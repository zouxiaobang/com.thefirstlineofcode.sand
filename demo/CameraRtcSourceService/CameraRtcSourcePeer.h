
#ifndef CAMERA_RTC_SOURCE_PEER_H
#define CAMERA_RTC_SOURCE_PEER_H

#include <iostream>
#include <string>

#include "api/peer_connection_interface.h"

using namespace std;

class ICameraRtcSourcePeer {
public:
	virtual char *getVideoCaptureDeviceName() = 0;
	virtual void open() = 0;
	virtual void close() = 0;
	virtual rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> getPeerConnectionFactory() = 0;
};

class CameraRtcSourcePeer : public ICameraRtcSourcePeer, public webrtc::PeerConnectionObserver {
public:
	CameraRtcSourcePeer();

 	void open();
	void close();
	rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> getPeerConnectionFactory();
	char *getVideoCaptureDeviceName();

	~CameraRtcSourcePeer();

	void OnAddTrack();
	void OnSignalingChange(webrtc::PeerConnectionInterface::SignalingState newState);
	void OnAddStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream);
	void OnRemoveStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream);
	void OnDataChannel(rtc::scoped_refptr<webrtc::DataChannelInterface> dataChannel);
	void OnRenegotiationNeeded();
	void OnIceConnectionChange(webrtc::PeerConnectionInterface::IceConnectionState new_state);
	void OnStandardizedIceConnectionChange(webrtc::PeerConnectionInterface::IceConnectionState new_state);
	void OnConnectionChange(webrtc::PeerConnectionInterface::PeerConnectionState new_state);
	void OnIceGatheringChange(webrtc::PeerConnectionInterface::IceGatheringState new_state);
	void OnIceCandidate(const webrtc::IceCandidateInterface* candidate);
	void OnIceCandidateError(const std::string& host_candidate, const std::string& url,
		int error_code, const std::string& error_text);
	void OnIceCandidateError(const std::string& address, int port,
		const std::string& url, int error_code, const std::string& error_text);
	void OnIceCandidatesRemoved(const std::vector<cricket::Candidate>& candidates);
	void OnIceConnectionReceivingChange(bool receiving);
	void OnIceSelectedCandidatePairChanged(cricket::CandidatePairChangeEvent& event);
	void OnAddTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver,
		const std::vector<rtc::scoped_refptr<webrtc::MediaStreamInterface>>& streams);
	void OnTrack(rtc::scoped_refptr<webrtc::RtpTransceiverInterface> transceiver);
	void OnRemoveTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver);
	void OnInterestingUsage(int usage_pattern);

private:
	void createPeerConnectionFactory();
private:
	rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> peerConnectionFactory;
	unique_ptr<rtc::Thread> networkThread;
	unique_ptr<rtc::Thread> workerThread;
	unique_ptr<rtc::Thread> signalingThread;

	char *videoCaptureDeviceName;
};

#endif