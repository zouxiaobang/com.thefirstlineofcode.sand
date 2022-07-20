package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

public class CameraRtcSourcePeer implements ICameraRtcSourcePeer {
	private CameraRtcSourcePeerClient cameraRtcSourcePeerClient;
	private ICameraRtcSourcePeer.Listener listener;
	
	public CameraRtcSourcePeer(CameraRtcSourcePeerClient cameraRtcSourcePeerClient,
			ICameraRtcSourcePeer.Listener listener) {
		this.cameraRtcSourcePeerClient = cameraRtcSourcePeerClient;
		this.listener = listener;
	}

	@Override
	public boolean isConnected() {
		return cameraRtcSourcePeerClient.isConnected();
	}
	
	@Override
	public boolean isClosed() {
		return !cameraRtcSourcePeerClient.isConnected();
	}
	
	@Override
	public void close() {
		cameraRtcSourcePeerClient.close();
	}
}
