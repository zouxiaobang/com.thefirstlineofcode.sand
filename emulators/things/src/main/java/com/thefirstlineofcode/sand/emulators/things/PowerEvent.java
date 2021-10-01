package com.thefirstlineofcode.sand.emulators.things;

import com.thefirstlineofcode.sand.emulators.things.emulators.IThingEmulator;

public class PowerEvent {
	public enum Type {
		POWER_ON,
		POWER_OFF
	}
	
	private IThingEmulator source;
	private Type type;
	
	public PowerEvent(IThingEmulator source, Type type) {
		this.source = source;
		this.type = type;
	}

	public IThingEmulator getSource() {
		return source;
	}
	
	public Type getType() {
		return type;
	}
	
}
