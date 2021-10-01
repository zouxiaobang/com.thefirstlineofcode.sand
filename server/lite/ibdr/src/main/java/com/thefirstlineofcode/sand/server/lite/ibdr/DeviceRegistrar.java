package com.thefirstlineofcode.sand.server.lite.ibdr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.thefirstlineofcode.granite.framework.core.annotations.AppComponent;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;
import com.thefirstlineofcode.sand.server.ibdr.DeviceRegistrationEvent;
import com.thefirstlineofcode.sand.server.ibdr.IDeviceRegistrar;
import com.thefirstlineofcode.sand.server.ibdr.IDeviceRegistrationCustomizerProxy;

@AppComponent("device.registrar")
public class DeviceRegistrar implements IDeviceRegistrar {
	private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrar.class);
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IDeviceRegistrationCustomizerProxy registrationCustomizerProxy;
	
	@Override
	public DeviceRegistrationEvent register(String deviceId) {
		if (!deviceManager.isValid(deviceId))
			throw new ProtocolException(new NotAcceptable(String.format("Invalid device ID '%s'.", deviceId)));

		DeviceIdentity identity = deviceManager.register(deviceId);
		if (logger.isInfoEnabled())
			logger.info("Device which's device ID is '{}' has registered. It's device name is assigned to '{}'.", deviceId, identity.getDeviceName());
		
		if (!registrationCustomizerProxy.isBinded())
			return new DeviceRegistrationEvent(identity);
		
		return new DeviceRegistrationEvent(identity, registrationCustomizerProxy.executeCustomizedTask(deviceId, identity));
	}

	@Override
	public void remove(String deviceId) {
		// TODO
	}
	
}
