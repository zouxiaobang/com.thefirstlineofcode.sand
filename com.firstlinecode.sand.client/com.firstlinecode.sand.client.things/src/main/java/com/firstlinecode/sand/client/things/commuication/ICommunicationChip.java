package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationChip<A, D> {
	void changeAddress(A address) throws CommunicationException;
	A getAddress();
	void send(A to, D data) throws CommunicationException;
	Data<A, D> receive();
	void addListener(ICommunicationListener<A, D> listener);
	boolean removeListener(ICommunicationListener<A, D> listener);
}
