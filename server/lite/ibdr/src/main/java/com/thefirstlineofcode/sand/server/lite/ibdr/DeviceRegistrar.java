package com.thefirstlineofcode.sand.server.lite.ibdr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAcceptable;
import com.thefirstlineofcode.granite.framework.core.annotations.AppComponent;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.sand.server.devices.DeviceRegistered;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;
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
	public DeviceRegistered register(String deviceId) {
		if (!deviceManager.isValid(deviceId))
			throw new ProtocolException(new NotAcceptable(String.format("Invalid device ID '%s'.", deviceId)));

		DeviceRegistered registered = deviceManager.register(deviceId);
		if (logger.isInfoEnabled())
			logger.info("Device which's device ID is '{}' has registered. It's device name is assigned to '{}'.",
					deviceId, registered.deviceIdentity.getDeviceName());

		return registered;
	}

	@Override
	public void remove(String deviceId) {
		// TODO
	}
	
}
