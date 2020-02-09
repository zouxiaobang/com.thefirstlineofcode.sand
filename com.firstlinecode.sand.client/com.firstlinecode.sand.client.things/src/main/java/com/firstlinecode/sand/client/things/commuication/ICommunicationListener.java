package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationListener<A, D> {
	void sent(A to, D data);
	void received(A from, D data);
	void occurred(CommunicationException e);
}
