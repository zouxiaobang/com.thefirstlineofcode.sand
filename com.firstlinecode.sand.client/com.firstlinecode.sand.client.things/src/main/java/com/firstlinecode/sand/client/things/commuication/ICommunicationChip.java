package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationChip<T> {
	void changeAddress(T address);
	T getAddress();
	void send(T to, byte[] message);
	void received(T from, byte[] message);
	void addListener(ICommunicationListener<T> listener);
	boolean removeListener(ICommunicationListener<T> listener);
}
