package com.thefirstlineofcode.sand.client.things.commuication;

public interface ICommunicator<OA, PA, D> {
	void changeAddress(OA address) throws CommunicationException;
	OA getAddress();
	void send(PA to, D data) throws CommunicationException;
	void received(PA from, D data);
	void addCommunicationListener(ICommunicationListener<OA, PA, D> listener);
	void removeCommunicationListener(ICommunicationListener<OA, PA, D> listener);
}
