package com.thefirstlineofcode.sand.server.ibdr;

import org.pf4j.ExtensionPoint;

import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public interface IDeviceRegistrationCustomizer extends ExtensionPoint {
	Object executeCustomizedTask(String deviceId, DeviceIdentity identity);
}
