package com.firstlinecode.sand.client.concentrator;

import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public interface IActionDeliverer<PA, D> {
	void setCommunicator(ICommunicator<?, PA, D> communicator);
	void deliver(PA to, Object action) throws CommunicationException;
}
