package com.firstlinecode.sand.client.things.concentrator;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public interface IAddressConfigurator {
	IDevice getDevice();
	void setCommunicator(ICommunicator<?, ?> communicator);
	void start();
	void stop();
}
