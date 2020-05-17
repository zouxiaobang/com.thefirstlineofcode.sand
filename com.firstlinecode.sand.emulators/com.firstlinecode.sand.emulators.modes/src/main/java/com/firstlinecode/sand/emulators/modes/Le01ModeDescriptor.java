package com.firstlinecode.sand.emulators.modes;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.protocols.emulators.light.Flash;

public class Le01ModeDescriptor extends ModeDescriptor {
	private static final String MODE_NAME = "LE01";

	public Le01ModeDescriptor() {
		super(MODE_NAME, false, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Flash.PROTOCOL, Flash.class);
		
		return supportedActions;
	}
}
