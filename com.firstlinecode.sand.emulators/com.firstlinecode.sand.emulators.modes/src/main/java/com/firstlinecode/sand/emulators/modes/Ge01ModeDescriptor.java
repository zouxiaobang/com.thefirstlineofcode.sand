package com.firstlinecode.sand.emulators.modes;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.protocols.emulators.gateway.Restart;

public class Ge01ModeDescriptor extends ModeDescriptor {
	private static final String MODE_NAME = "GE01";
	private static final String ACTION_NAME_RESTART = "restart";

	public Ge01ModeDescriptor() {
		super(MODE_NAME, true, createSupportedActions(), null);
	}

	private static Map<String, Class<?>> createSupportedActions() {
		Map<String, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(ACTION_NAME_RESTART, Restart.class);
		
		return supportedActions;
	}
}
