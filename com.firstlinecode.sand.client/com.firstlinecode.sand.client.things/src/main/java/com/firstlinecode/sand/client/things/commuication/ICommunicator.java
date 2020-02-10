package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicator<A, D> {
	void changeAddress(A address);
	A getAddress();
	void send(A to, D data);
	void received(A from, D data);
	void addCommunicationListener(ICommunicationListener<A, D> listener);
	void removeCommunicationListener(ICommunicationListener<A, D> listener);
}