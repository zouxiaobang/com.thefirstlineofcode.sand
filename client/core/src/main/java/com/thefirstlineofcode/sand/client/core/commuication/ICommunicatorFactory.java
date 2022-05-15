package com.thefirstlineofcode.sand.client.core.commuication;

public interface ICommunicatorFactory {
	ICommunicator<?, ?, ?> createCommunicator(ParamsMap params);
}
