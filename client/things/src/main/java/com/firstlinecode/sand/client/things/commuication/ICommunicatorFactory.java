package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicatorFactory {
	ICommunicator<?, ?, ?> createCommunicator(ParamsMap params);
}
