package com.firstlinecode.sand.emulators.models;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.protocols.devices.gateway.Restart;

public class Ge01ModelDescriptor extends ModelDescriptor {
	private static final String MODEL_NAME = "GE01";

	public Ge01ModelDescriptor() {
		super(MODEL_NAME, true, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Restart.PROTOCOL, Restart.class);
		
		return supportedActions;
	}
}
