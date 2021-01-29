package com.firstlinecode.sand.emulators.models;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.protocols.emulators.light.Flash;

public class Le02ModelDescriptor extends ModelDescriptor {
	private static final String MODEL_NAME = "LE02";

	public Le02ModelDescriptor() {
		super(MODEL_NAME, false, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Flash.PROTOCOL, Flash.class);
		
		return supportedActions;
	}
}
