package com.thefirstlineofcode.sand.emulators.models;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.things.simple.light.Flash;
import com.thefirstlineofcode.sand.protocols.things.simple.light.TurnOff;
import com.thefirstlineofcode.sand.protocols.things.simple.light.TurnOn;

public class SlWe01ModelDescriptor extends ModelDescriptor {
	public static final String MODEL_NAME = "SL-WE01";
	public static final String THING_TYPE = "Simple Light WiFi Emulator";

	public SlWe01ModelDescriptor() {
		super(MODEL_NAME, true, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Flash.PROTOCOL, Flash.class);
		supportedActions.put(TurnOn.PROTOCOL, TurnOn.class);
		supportedActions.put(TurnOff.PROTOCOL, TurnOff.class);
		
		return supportedActions;
	}
}
