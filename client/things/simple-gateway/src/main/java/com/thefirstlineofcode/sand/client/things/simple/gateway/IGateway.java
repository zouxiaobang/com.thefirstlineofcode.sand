package com.thefirstlineofcode.sand.client.things.simple.gateway;

import com.thefirstlineofcode.sand.client.core.IDevice;

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
