package com.thefirstlineofcode.sand.emulators.models;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.devices.light.Flash;

public class Ld01ModelDescriptor extends ModelDescriptor {
	public static final String MODEL_NAME = "LD01";

	public Ld01ModelDescriptor() {
		super(MODEL_NAME, true, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Flash.PROTOCOL, Flash.class);
		
		return supportedActions;
	}
}
