package com.firstlinecode.sand.emulators.things;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.emulators.things.emulators.IThingEmulatorFactory;

public interface IGateway extends IDevice {
	void registerThingEmulatorFactory(IThingEmulatorFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
	String getLanId();
}
