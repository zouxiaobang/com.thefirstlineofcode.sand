package com.firstlinecode.sand.client.things.commuication;

public interface IAddressConfigurator<C extends ICommunicator<?, PA, D>, PA, D> {
	void setCommunicator(C communicator);
	void introduce();
	void negotiate(PA peerAddress, D data);
	void confirm();
}
