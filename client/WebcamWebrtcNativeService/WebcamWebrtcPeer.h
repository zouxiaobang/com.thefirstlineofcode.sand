
#ifndef CAMERA_RTC_SOURCE_PEER_H
#define CAMERA_RTC_SOURCE_PEER_H

#include <iostream>
#include <string>

#include "api/peer_connection_interface.h"
#include "api/scoped_refptr.h"
#include "rtc_base/ref_count.h"
#include "pc/video_track_source.h"
#include "modules/video_capture/video_capture_factory.h"
#include "api/media_stream_interface.h"
#include "vcm_capturer.h"

#include "cppnet.h"

using namespace std;

class CapturerTrackSource : public webrtc::VideoTrackSource {
public:
	static rtc::scoped_refptr<CapturerTrackSource> Create() {
		std::unique_ptr<webrtc::test::VcmCapturer> capturer;
		std::unique_ptr<webrtc::VideoCaptureModule::DeviceInfo> info(
			webrtc::VideoCaptureFactory::CreateDeviceInfo());
		if (!info) {
			return nullptr;
		}

		const size_t width = 640;
		const size_t height = 480;
		const size_t fps = 30;
		int numDevices = info->NumberOfDevices();
		for (int i = 0; i < numDevices; ++i) {
			capturer = absl::WrapUnique(
				webrtc::test::VcmCapturer::Create(width, height, fps, i));
			if (capturer) {
				return new rtc::RefCountedObject<CapturerTrackSource>(std::move(capturer));
			}
		}

		return nullptr;
	}

	CapturerTrackSource::~CapturerTrackSource() {
		cout << "Call destructor of CapturerTrackSource." << endl;
		pCapturer = nullptr;
	}

protected:
	explicit CapturerTrackSource(std::unique_ptr<webrtc::test::VcmCapturer> _capturer)
			: VideoTrackSource(/*remote=*/false), capturer(std::move(_capturer)) {
		cout << "Capturer get: " << capturer.get() << "." << endl;
		pCapturer = capturer.get();
	}

	rtc::VideoSourceInterface<webrtc::VideoFrame> *source() override {
		if (capturer.get())
			return capturer.get();

		return pCapturer;
	}

private:
	std::unique_ptr<webrtc::test::VcmCapturer> capturer;
	webrtc::test::VcmCapturer *pCapturer;
};

class IWebcamWebrtcPeer {
public:
	virtual const char *getVideoCaptureDeviceName() = 0;
	virtual void open() = 0;
	virtual void close() = 0;
	virtual bool isOpened() = 0;
	virtual bool isClosed() = 0;
	virtual void offered(cppnet::Handle handle, std::string offerSdp) = 0;
};

class WebcamWebrtcPeer : public IWebcamWebrtcPeer,
	public webrtc::PeerConnectionObserver,
	public rtc::RefCountInterface {
public:
	WebcamWebrtcPeer();

 	void open();
	void close();
	bool isOpened();
	bool isClosed();
	const char *getVideoCaptureDeviceName();
	void offered(cppnet::Handle handle, std::string offerSdp);

	~WebcamWebrtcPeer();

	void OnSignalingChange(webrtc::PeerConnectionInterface::SignalingState newState);
	void OnAddStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream);
	void OnRemoveStream(rtc::scoped_refptr<webrtc::MediaStreamInterface> stream);
	void OnDataChannel(rtc::scoped_refptr<webrtc::DataChannelInterface> dataChannel);
	void OnRenegotiationNeeded();
	void OnIceConnectionChange(webrtc::PeerConnectionInterface::IceConnectionState new_state);
	void OnStandardizedIceConnectionChange(webrtc::PeerConnectionInterface::IceConnectionState new_state);
	void OnConnectionChange(webrtc::PeerConnectionInterface::PeerConnectionState new_state);
	void OnIceGatheringChange(webrtc::PeerConnectionInterface::IceGatheringState new_state);
	void OnIceCandidate(const webrtc::IceCandidateInterface *candidate);
	void OnIceCandidateError(const std::string &host_candidate, const std::string &url,
		int error_code, const std::string &error_text);
	void OnIceCandidateError(const std::string &address, int port,
		const std::string& url, int error_code, const std::string &error_text);
	void OnIceCandidatesRemoved(const std::vector<cricket::Candidate> &candidates);
	void OnIceConnectionReceivingChange(bool receiving);
	void OnIceSelectedCandidatePairChanged(cricket::CandidatePairChangeEvent &event);
	void OnAddTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver,
		const std::vector<rtc::scoped_refptr<webrtc::MediaStreamInterface>> &streams);
	void OnTrack(rtc::scoped_refptr<webrtc::RtpTransceiverInterface> transceiver);
	void OnRemoveTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver);
	void OnInterestingUsage(int usage_pattern);

	class VideoRenderer : public rtc::VideoSinkInterface<webrtc::VideoFrame> {
	public:
		VideoRenderer(webrtc::VideoTrackInterface *_trackToRender) :
				trackToRender(_trackToRender) {
			trackToRender->AddOrUpdateSink(this, rtc::VideoSinkWants());
		}
		virtual ~VideoRenderer() {
			trackToRender->RemoveSink(this);
		}

		// VideoSinkInterface implementation
		void OnFrame(const webrtc::VideoFrame &frame) {
			cout << "Call OnFrame. Update rect: [" <<
				frame.update_rect().offset_x << ", " <<
				frame.update_rect().offset_y << ", " <<
				frame.update_rect().width << ", " <<
				frame.update_rect().height << "]." << endl;
		}

	private:
		rtc::scoped_refptr<webrtc::VideoTrackInterface> trackToRender;
	};

private:
	void createPeerConnectionFactory();
	void createPeerConnection();
	void addVideoTrack();
	void startVideoRenderer(webrtc::VideoTrackInterface *videoTrack);
	void stopVideoRenderer();
private:
	const char *labelVideoStream = "video_only_stream";
	const char *labelVideoTrack = "video_track";
	rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> peerConnectionFactory;
	rtc::scoped_refptr<webrtc::PeerConnectionInterface> peerConnection;
	rtc::scoped_refptr<CapturerTrackSource> videoDevice;
	rtc::scoped_refptr<webrtc::VideoTrackInterface> videoTrack;
	std::unique_ptr<VideoRenderer> localRenderer;

	char *videoCaptureDeviceName;
	bool opened;
};

#endif