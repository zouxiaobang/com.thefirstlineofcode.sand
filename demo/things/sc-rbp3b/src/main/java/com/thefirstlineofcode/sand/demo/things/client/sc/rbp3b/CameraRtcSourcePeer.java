package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

public class CameraRtcSourcePeer implements ICameraRtcSourcePeer {
	private CameraRtcSourcePeerClient client;
	private Listener listener;
	
	public CameraRtcSourcePeer(CameraRtcSourcePeerClient client) {
		this.client = client;
	}

	@Override
	public boolean isConnected() {
		return client.isConnected();
	}
	
	@Override
	public boolean isClosed() {
		return !client.isConnected();
	}
	
	@Override
	public void close() {
		client.close();
	}
	
	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	@Override
	public void removeListener() {
		listener = null;
	}
	
	public void processSourcePeerMessage(String message) {
		System.out.println("Process source peer message: " + message);
	}
	
	@Override
	public void test() {
		// TODO Auto-generated method stub
		client.send("Hello");
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		client.send("World");
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
