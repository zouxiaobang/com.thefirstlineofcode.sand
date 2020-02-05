package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationListener<T> {
	void sent(T to, byte[] message);
	void received(T from, byte[] message);
	void occurred(CommunicationException e);
}
