package com.firstlinecode.sand.protocols.core;

import java.util.Map;

public class ModeDescriptor {
	private boolean actuator;
	private boolean concentrator;
	private boolean sensor;
	
	private Map<String, Class<?>> supportedActions;
	private Map<String, Class<?>> supportedEvents;
	
	public ModeDescriptor() {}
	
	public ModeDescriptor(boolean concentrator) {
		this(true, null, null);
	}
	
	public ModeDescriptor(Map<String, Class<?>> suppportedActions, Map<String, Class<?>> supportedEvents) {
		this(false, suppportedActions, supportedEvents);
	}
	
	public ModeDescriptor(boolean concentrator, Map<String, Class<?>> suppportedActions, Map<String, Class<?>> supportedEvents) {
		this.concentrator = concentrator;
		this.supportedActions = suppportedActions;
		this.supportedEvents = supportedEvents;
		
		if (suppportedActions != null) {
			actuator = true;
		}
		
		if (supportedEvents != null) {
			sensor = true;
		}
	}
	
	public boolean isActuator() {
		return actuator;
	}
	
	public void setActuator(boolean actuator) {
		this.actuator = actuator;
	}
	
	public boolean isConcentrator() {
		return concentrator;
	}
	
	public void setConcentrator(boolean concentrator) {
		this.concentrator = concentrator;
	}
	
	public boolean isSensor() {
		return sensor;
	}
	
	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}
	
	public Map<String, Class<?>> getSupportedActions() {
		return supportedActions;
	}
	
	public Map<String, Class<?>> getSupportedEvents() {
		return supportedEvents;
	}
}
