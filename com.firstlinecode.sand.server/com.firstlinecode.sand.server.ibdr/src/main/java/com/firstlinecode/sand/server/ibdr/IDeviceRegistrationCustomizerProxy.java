package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceRegistrationCustomizerProxy {
	Object executeCustomizedTask(String deviceId, DeviceIdentity identity);
	void processResult(IClientConnectionContext context, Object result);
	IDeviceRegistrationCustomizer getDeviceRegistrationCustomizer();
}
