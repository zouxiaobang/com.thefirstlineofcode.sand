package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.granite.framework.core.annotations.Component;

@Component("default.device.registrar")
public class Registrar implements IDeviceRegistrar {
	@Override
	public JabberId register(String device) {
		// TODO
		return null;
	}
	
	@Override
	public void remove(String username) {
		// TODO
	}
	
}
