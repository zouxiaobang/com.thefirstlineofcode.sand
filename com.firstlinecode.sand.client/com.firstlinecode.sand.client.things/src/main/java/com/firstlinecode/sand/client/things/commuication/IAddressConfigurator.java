package com.firstlinecode.sand.client.things.commuication;

public interface IAddressConfigurator<C extends ICommunicator<?, TA, D>, TA, D> {
	void setCommunicator(C communicator);
	void introduce();
	void negotiate(TA targetAddress, D data);
	void confirm();
}
