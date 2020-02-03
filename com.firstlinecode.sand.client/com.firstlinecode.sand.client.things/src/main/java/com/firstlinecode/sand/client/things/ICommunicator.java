package com.firstlinecode.sand.client.things;

public interface ICommunicator<K, V> {
	void send(K address, V message);
	void received(K from, V message);
	void addCommunicationListener(ICommunicationListener<K, V> listener);
}
