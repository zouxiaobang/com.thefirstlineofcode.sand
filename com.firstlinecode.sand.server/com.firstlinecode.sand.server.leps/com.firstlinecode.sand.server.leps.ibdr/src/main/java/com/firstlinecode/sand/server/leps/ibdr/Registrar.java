package com.firstlinecode.sand.server.leps.ibdr;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.granite.framework.core.annotations.Component;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.framework.auth.IDeviceManager;

@Component("default.device.registrar")
public class Registrar implements IDeviceRegistrar {
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Override
	public DeviceIdentity register(String deviceId) {
		if (!isValidDeviceId(deviceId))
			throw new ProtocolException(new NotAcceptable());
		
		return deviceManager.register(deviceId);
	}
	
	protected boolean isValidDeviceId(String deviceId) {
		return deviceId.length() == 12;
	}

	@Override
	public void remove(String deviceId) {
		// TODO
	}
	
}
