package com.thefirstlineofcode.sand.client.core.commuication;

import com.thefirstlineofcode.sand.protocols.core.Address;

public interface IAddressConfigurator<C extends ICommunicator<?, PA, D>, PA extends Address, D> {
	void setCommunicator(C communicator);
	void introduce();
	void negotiate(PA peerAddress, D data);
}
