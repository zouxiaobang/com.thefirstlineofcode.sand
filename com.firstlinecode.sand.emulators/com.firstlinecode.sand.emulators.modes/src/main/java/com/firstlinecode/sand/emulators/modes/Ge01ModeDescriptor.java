package com.firstlinecode.sand.emulators.modes;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.protocols.emulators.gateway.Restart;

public class Ge01ModeDescriptor extends ModeDescriptor {
	private static final String MODE_NAME = "GE01";

	public Ge01ModeDescriptor() {
		super(MODE_NAME, true, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Restart.PROTOCOL, Restart.class);
		
		return supportedActions;
	}
}
