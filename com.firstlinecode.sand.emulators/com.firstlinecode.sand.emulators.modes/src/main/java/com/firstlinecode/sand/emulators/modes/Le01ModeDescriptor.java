package com.firstlinecode.sand.emulators.modes;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.sand.protocols.core.IAction;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.protocols.emulators.light.Flash;

public class Le01ModeDescriptor extends ModeDescriptor {
	private static final String MODE_NAME = "LE01";
	private static final String ACTION_NAME_FLASH = "flash";

	public Le01ModeDescriptor() {
		super(MODE_NAME, true, createSupportedActions(), null);
	}

	private static Map<String, Class<? extends IAction>> createSupportedActions() {
		Map<String, Class<? extends IAction>> supportedActions = new HashMap<>();
		supportedActions.put(ACTION_NAME_FLASH, Flash.class);
		
		return supportedActions;
	}
}
