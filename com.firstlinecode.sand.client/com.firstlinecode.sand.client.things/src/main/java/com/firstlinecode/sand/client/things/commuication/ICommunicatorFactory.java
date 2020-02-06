package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicatorFactory<A, D> {
	ICommunicator<A, D> createCommunicator();
}
