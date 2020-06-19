package com.firstlinecode.sand.server.lite.ibdr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizer;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizerProxy;

@Component
@Transactional
public class DeviceRegistrationCustomizerProxy implements IDeviceRegistrationCustomizerProxy {
	@Autowired(required = false)
	private IDeviceRegistrationCustomizer real;

	@Override
	public Object executeCustomizedTask(String deviceId, DeviceIdentity identity) {
		if (real == null)
			return null;
		
		return real.executeCustomizedTask(deviceId, identity);
	}

	@Override
	public void processResult(IClientConnectionContext context, Object result) {
		// TODO Auto-generated method stub
		if (real == null)
			return;
		
		real.processResult(context, result);
	}

}
