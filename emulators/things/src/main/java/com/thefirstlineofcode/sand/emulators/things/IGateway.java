package com.thefirstlineofcode.sand.emulators.things;

import com.thefirstlineofcode.sand.client.things.IDevice;
import com.thefirstlineofcode.sand.emulators.things.emulators.IThingEmulatorFactory;

public interface IGateway extends IDevice {
	void registerThingEmulatorFactory(IThingEmulatorFactory<?> factory);
	boolean isRegistered();
	boolean isConnected();
	void register();
	void connect();
	void disconnect();
	void setToWorkingMode();
	void setToAddressConfigurationMode();
	String getLanId();
}
