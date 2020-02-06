package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.emulators.thing.IThingEmulatorFactory;

public interface IGateway<T> extends IConcentrator<T> {
	void registerThingEmulatorFactory(IThingEmulatorFactory<?> factory);
	IAddressConfigurator getAddressConfigurator();
	boolean isRegistered();
	boolean isConnected();
}
