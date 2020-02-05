package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicator<A, D> {
	public enum CommunicationMode {
		ADDRESS_CONFIGURATION,
		WORKING
	}
	
	A getAddress();
	void setCommunicationMode(CommunicationMode communicationMode);
	CommunicationMode getCommunicationMode();
	void send(A to, D data);
	void received(A from, D data);
	void addCommunicationListener(ICommunicationListener<A> listener);
}
