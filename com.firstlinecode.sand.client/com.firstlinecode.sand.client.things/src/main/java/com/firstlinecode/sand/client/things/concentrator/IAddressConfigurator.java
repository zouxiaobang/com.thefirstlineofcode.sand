package com.firstlinecode.sand.client.things.concentrator;

public interface IAddressConfigurator<C, A, D> {
	void setCommunicator(C communicator);
	void introduce();
	void negotiate(A peerAddress, D data);
	void comfirm(A peerAddress);
}
