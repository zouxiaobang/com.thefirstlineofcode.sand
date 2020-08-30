package com.firstlinecode.sand.emulators.modes;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.emulators.thing.IThingEmulatorFactory;

public interface IGateway extends IDevice {
	void registerThingEmulatorFactory(IThingEmulatorFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
	String getLanId();
}
