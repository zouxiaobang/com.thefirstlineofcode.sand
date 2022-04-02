package com.thefirstlineofcode.sand.client.things.commuication;

import com.thefirstlineofcode.sand.protocols.core.Address;

public interface ICommunicationListener<OA, PA extends Address, D> {
	void sent(PA to, D data);
	void received(PA from, D data);
	void occurred(CommunicationException e);
	void addressChanged(OA newAddress, OA oldAddress);
}
