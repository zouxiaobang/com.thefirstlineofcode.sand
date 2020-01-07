package com.firstlinecode.sand.emulators.thing;

public class PowerEvent {
	public enum Type {
		POWER_ON,
		POWER_OFF
	}
	
	private IThing source;
	private Type type;
	
	public PowerEvent(IThing source, Type type) {
		this.source = source;
		this.type = type;
	}

	public IThing getSource() {
		return source;
	}
	
	public Type getType() {
		return type;
	}
	
}
