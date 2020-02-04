package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.emulators.thing.IThingFactory;

public interface IGateway extends IDevice {
	void registerThingFactory(IThingFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
}
