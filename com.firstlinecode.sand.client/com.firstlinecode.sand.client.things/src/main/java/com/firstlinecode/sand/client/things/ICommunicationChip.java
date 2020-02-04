package com.firstlinecode.sand.client.things;

public interface ICommunicationChip<T> {
	void changeAddress(T address);
	T getAddress();
	void send(T to, byte[] message);
	void received(T from, byte[] message);
}
