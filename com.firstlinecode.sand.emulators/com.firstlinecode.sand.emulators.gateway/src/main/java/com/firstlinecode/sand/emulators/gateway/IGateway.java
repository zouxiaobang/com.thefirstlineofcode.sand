package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.emulators.thing.IThingEmulatorFactory;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public interface IGateway extends IConcentrator<LoraAddress> {
	void registerThingEmulatorFactory(IThingEmulatorFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
	String getLanId();
}
