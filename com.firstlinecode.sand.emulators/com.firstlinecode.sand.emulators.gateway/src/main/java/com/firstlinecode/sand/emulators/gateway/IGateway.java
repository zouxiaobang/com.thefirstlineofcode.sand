package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.emulators.thing.IThingEmulatorFactory;

public interface IGateway extends IConcentrator<LoraAddress> {
	void registerThingEmulatorFactory(IThingEmulatorFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
	String getLanId();
}
