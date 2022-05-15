package com.thefirstlineofcode.sand.client.core.commuication;

import com.thefirstlineofcode.sand.protocols.core.Address;

public interface ICommunicationChip<A extends Address, D> {
	void changeAddress(A address) throws CommunicationException;
	A getAddress();
	void send(A to, D data) throws CommunicationException;
	Data<A, D> receive();
	void addListener(ICommunicationListener<A, A, D> listener);
	boolean removeListener(ICommunicationListener<A, A, D> listener);
}
