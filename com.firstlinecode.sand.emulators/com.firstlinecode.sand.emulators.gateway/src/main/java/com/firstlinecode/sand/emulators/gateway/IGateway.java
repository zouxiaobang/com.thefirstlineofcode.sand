package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.emulators.thing.IThingFactory;

public interface IGateway {
	void registerThingFactory(IThingFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
}
