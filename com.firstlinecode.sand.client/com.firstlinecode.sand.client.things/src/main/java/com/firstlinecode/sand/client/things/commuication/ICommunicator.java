package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicator<K, V> {
	public enum CommunicationMode {
		ADDRESS_CONFIGURATION,
		WORKING
	}
	
	K getAddress();
	void setCommunicationMode(CommunicationMode communicationMode);
	CommunicationMode getCommunicationMode();
	void send(K to, V message);
	void received(K from, V message);
	void addCommunicationListener(ICommunicationListener<K, V> listener);
}
