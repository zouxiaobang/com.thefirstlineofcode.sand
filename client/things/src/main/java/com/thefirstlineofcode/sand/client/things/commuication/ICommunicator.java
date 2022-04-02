package com.thefirstlineofcode.sand.client.things.commuication;

import com.thefirstlineofcode.sand.protocols.core.Address;

public interface ICommunicator<OA, PA extends Address, D> {
	void changeAddress(OA address) throws CommunicationException;
	OA getAddress();
	void send(PA to, D data) throws CommunicationException;
	void received(PA from, D data);
	void addCommunicationListener(ICommunicationListener<OA, PA, D> listener);
	void removeCommunicationListener(ICommunicationListener<OA, PA, D> listener);
}
