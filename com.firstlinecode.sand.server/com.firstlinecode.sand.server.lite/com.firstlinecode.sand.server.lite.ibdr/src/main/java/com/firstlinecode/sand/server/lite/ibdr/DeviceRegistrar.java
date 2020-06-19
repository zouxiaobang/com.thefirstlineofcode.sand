package com.firstlinecode.sand.server.lite.ibdr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.device.IDeviceManager;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrar;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizer;

@Component
@Transactional
public class DeviceRegistrar implements IDeviceRegistrar {
	@Autowired
	private IDeviceManager deviceManager;
	
	@Autowired(required=false)
	private IDeviceRegistrationCustomizer registrationCustomizer;
	
	@Override
	public RegistrationResult register(String deviceId) {
		if (!deviceManager.isValid(deviceId))
			throw new ProtocolException(new NotAcceptable(String.format("Invalid device ID '%s'.", deviceId)));

		DeviceIdentity identity = deviceManager.register(deviceId);
		if (registrationCustomizer == null)
			return new RegistrationResult(identity);
		
		Object customizedTaskResult = registrationCustomizer.executeCustomizedTask(deviceId, identity);
		
		return new RegistrationResult(identity, customizedTaskResult);
	}

	@Override
	public void remove(String deviceId) {
		// TODO
	}
	
}
