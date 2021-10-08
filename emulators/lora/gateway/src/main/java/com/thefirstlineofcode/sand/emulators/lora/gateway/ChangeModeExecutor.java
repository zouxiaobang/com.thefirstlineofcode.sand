package com.thefirstlineofcode.sand.emulators.lora.gateway;

import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.sand.client.things.autuator.ExecutionException;
import com.thefirstlineofcode.sand.client.things.autuator.IExecutor;
import com.thefirstlineofcode.sand.protocols.devices.gateway.ChangeMode;

public class ChangeModeExecutor implements IExecutor<ChangeMode> {
	private Gateway<?, ?> gateway;
	
	public ChangeModeExecutor(Gateway<?, ?> gateway) {
		this.gateway = gateway;
	}

	@Override
	public void execute(Iq iq, ChangeMode changeMode) throws ExecutionException {
		if (changeMode.getMode() == ChangeMode.Mode.ADDRESS_CONFIGURATION)
			gateway.setToAddressConfigurationMode();
		else
			gateway.setToWorkingMode();
	}
}
