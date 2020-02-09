package com.firstlinecode.sand.client.things.concentrator;

public interface IAddressConfigurator<T> {
	void setCommunicator(T communicator);
	void introduce();
	void negotiate();
	void comfirm();
}
