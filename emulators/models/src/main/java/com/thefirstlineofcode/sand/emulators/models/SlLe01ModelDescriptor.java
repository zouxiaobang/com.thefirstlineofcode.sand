package com.thefirstlineofcode.sand.emulators.models;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.things.simple.light.Flash;

public class SlLe01ModelDescriptor extends ModelDescriptor {
	public static final String MODEL_NAME = "SL-LE01";
	public static final String THING_TYPE = "Simple Light Lora Emulator";

	public SlLe01ModelDescriptor() {
		super(MODEL_NAME, false, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Flash.PROTOCOL, Flash.class);
		
		return supportedActions;
	}
	
	@Override
	protected int calculateLanExecutionTimeout(Object action) {
		if (action instanceof Flash) {
			Flash flash = (Flash)action;
			
			return flash.getRepeat() * 2 + 2;
		}
		
		return super.calculateLanExecutionTimeout(action);
	}
}
