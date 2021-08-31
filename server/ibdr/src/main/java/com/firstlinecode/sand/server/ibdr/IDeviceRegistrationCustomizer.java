package com.firstlinecode.sand.server.ibdr;

import org.pf4j.ExtensionPoint;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceRegistrationCustomizer extends ExtensionPoint {
	Object executeCustomizedTask(String deviceId, DeviceIdentity identity);
}
