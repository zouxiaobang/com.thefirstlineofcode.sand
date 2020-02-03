package com.firstlinecode.sand.client.things;

public interface ICommunicationListener<K, V> {
	void sent(K address, V message);
	void received(K from, V message);
	void occurred(CommunicationException e);
}
