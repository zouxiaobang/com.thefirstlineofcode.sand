package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceRegistrationCustomizer {
	Object executeCustomizedTask(String deviceId, DeviceIdentity identity);
}
