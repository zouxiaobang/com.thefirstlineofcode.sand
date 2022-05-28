package com.thefirstlineofcode.sand.server.ibdr;

import org.pf4j.ExtensionPoint;

import com.thefirstlineofcode.sand.server.devices.DeviceRegistered;

public interface IDeviceRegistrationCustomizer extends ExtensionPoint {
	void executeCustomizedTask(DeviceRegistered registered);
}
