package com.firstlinecode.sand.client.dummything;

public class PowerEvent {
	public enum Type {
		POWER_ON,
		POWER_OFF
	}
	
	private IDummyThing source;
	private Type type;
	
	public PowerEvent(IDummyThing source, Type type) {
		this.source = source;
		this.type = type;
	}

	public IDummyThing getSource() {
		return source;
	}
	
	public Type getType() {
		return type;
	}
	
}
