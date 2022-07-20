package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

public interface ICameraRtcSourcePeer {
	public interface Listener {
		
	}
	
	void close();
	boolean isConnected();
	boolean isClosed();
}
