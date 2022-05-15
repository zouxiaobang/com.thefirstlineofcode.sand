package com.thefirstlineofcode.sand.client.devices.simple.gateway;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.protocols.devices.simple.gateway.ChangeMode;

public class ChangeModeExecutor implements IExecutor<ChangeMode> {
	private IGateway gateway;
	
	public ChangeModeExecutor(IGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public void execute(Iq iq, ChangeMode changeMode) throws ProtocolException {
		if (changeMode.getMode() == ChangeMode.Mode.ADDRESS_CONFIGURATION)
			gateway.setToAddressConfigurationMode();
		else
			gateway.setToWorkingMode();
	}
}
