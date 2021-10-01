package com.thefirstlineofcode.sand.client.things.concentrator;

import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;

public interface IActionDeliverer<PA, D> {
	void setCommunicator(ICommunicator<?, PA, D> communicator);
	void deliver(PA to, Object action) throws CommunicationException;
}
