package com.firstlinecode.sand.server.emulator.modes.internal;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.server.framework.things.IDeviceManager;

public class ModesRegistrar {
	// GE01: Gateway emulator 01
	private static final String MODE_GE01 = "GE01";
	// BE01: Light emulator 01
	private static final String MODE_BE01 = "LE01";
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	public void registerModes() {
		deviceManager.registerMode(MODE_GE01,
				new ModeDescriptor(true, getGE01SupportedActions(), getGE01SupportedEvents()));
		deviceManager.registerMode(MODE_BE01,
				new ModeDescriptor(true, getBE01SupportedActions(), getBE01SupportedEvents()));
	}
	
	private Map<String, Class<?>> getBE01SupportedEvents() {
		// TODO Auto-generated method stub
		return new HashMap<String, Class<?>>();
	}

	private Map<String, Class<?>> getBE01SupportedActions() {
		// TODO Auto-generated method stub
		return new HashMap<String, Class<?>>();
	}

	private Map<String, Class<?>> getGE01SupportedEvents() {
		// TODO Auto-generated method stub
		return new HashMap<String, Class<?>>();
	}

	private Map<String, Class<?>> getGE01SupportedActions() {
		// TODO Auto-generated method stub
		return new HashMap<String, Class<?>>();
	}

	public void unregisterModes() {
		deviceManager.unregisterMode(MODE_BE01);
		deviceManager.unregisterMode(MODE_GE01);
	}
}
