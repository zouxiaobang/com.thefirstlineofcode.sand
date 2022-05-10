package com.thefirstlineofcode.sand.emulators.things;

import com.thefirstlineofcode.sand.client.things.IDevice;

public interface IGateway extends IDevice {
	boolean isRegistered();
	boolean isConnected();
	void register();
	void connect();
	void disconnect();
	void setToWorkingMode();
	void setToAddressConfigurationMode();
	String getLanId();
}
