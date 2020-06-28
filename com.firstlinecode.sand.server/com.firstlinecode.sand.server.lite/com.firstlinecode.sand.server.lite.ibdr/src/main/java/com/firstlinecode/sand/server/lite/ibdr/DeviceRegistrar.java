package com.firstlinecode.sand.server.lite.ibdr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.device.IDeviceManager;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrar;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizerProxy;

@Component
@Transactional
public class DeviceRegistrar implements IDeviceRegistrar {
	@Autowired
	private IDeviceManager deviceManager;
	
	@Autowired
	private IDeviceRegistrationCustomizerProxy registrationCustomizerProxy;
	
	@Override
	public RegistrationResult register(String deviceId) {
		if (!deviceManager.isValid(deviceId))
			throw new ProtocolException(new NotAcceptable(String.format("Invalid device ID '%s'.", deviceId)));

		DeviceIdentity identity = deviceManager.register(deviceId);
		if (!registrationCustomizerProxy.isBinded())
			return new RegistrationResult(identity);
		
		Object customizedTaskResult = registrationCustomizerProxy.executeCustomizedTask(deviceId, identity);
		
		return new RegistrationResult(identity, customizedTaskResult);
	}

	@Override
	public void remove(String deviceId) {
		// TODO
	}
	
}
