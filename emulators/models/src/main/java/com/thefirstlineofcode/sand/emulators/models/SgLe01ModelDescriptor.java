package com.thefirstlineofcode.sand.emulators.models;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.things.simple.gateway.ChangeMode;

public class SgLe01ModelDescriptor extends ModelDescriptor {
	public static final String MODEL_NAME = "SG-LE01";
	public static final String THING_TYPE = "Simple Gateway Lora Emulator";

	public SgLe01ModelDescriptor() {
		super(MODEL_NAME, true, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(ChangeMode.PROTOCOL, ChangeMode.class);
		
		return supportedActions;
	}
}
