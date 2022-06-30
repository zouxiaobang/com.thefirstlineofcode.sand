package com.thefirstlineofcode.sand.client.things.simple.gateway;

import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.protocols.things.simple.gateway.ChangeMode;

public class ChangeModeExecutor implements IExecutor<ChangeMode> {
	private IGateway gateway;
	
	public ChangeModeExecutor(IGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public Object execute(Iq iq, ChangeMode changeMode) throws ProtocolException {
		if (changeMode.getMode() == ChangeMode.Mode.ADDRESS_CONFIGURATION)
			gateway.setToAddressConfigurationMode();
		else
			gateway.setToWorkingMode();
		
		return null;
	}
}
