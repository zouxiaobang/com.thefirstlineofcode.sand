package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationListener<OA, PA, D> {
	void sent(PA to, D data);
	void received(PA from, D data);
	void occurred(CommunicationException e);
	void addressChanged(OA newAddress, OA oldAddress);
}
