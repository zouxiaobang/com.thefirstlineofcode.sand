package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.IDevice;

public interface IGateway extends IDevice {
	void registerThingEmulatorFactory(IThingEmulatorFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
	String getLanId();
}
