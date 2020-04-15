package com.firstlinecode.sand.protocols.core;

import java.util.Map;

public class ModeDescriptor {
	private String name;
	private boolean actuator;
	private boolean concentrator;
	private boolean sensor;
	
	private Map<String, Class<? extends IAction>> supportedActions;
	private Map<String, Class<? extends IEvent>> supportedEvents;
	
	public ModeDescriptor(String name) {
		this(name, false);
	}
	
	public ModeDescriptor(String name, boolean concentrator) {
		this(name, concentrator, null, null);
	}
	
	public ModeDescriptor(String name, Map<String, Class<? extends IAction>> suppportedActions, Map<String, Class<? extends IEvent>> supportedEvents) {
		this(name, false, suppportedActions, supportedEvents);
	}
	
	public ModeDescriptor(String name, boolean concentrator, Map<String, Class<? extends IAction>> suppportedActions, Map<String, Class<? extends IEvent>> supportedEvents) {
		this.name = name;
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
	
	public String getName() {
		return name;
	}
	
	public boolean isActuator() {
		return actuator;
	}
	
	public boolean isConcentrator() {
		return concentrator;
	}
	
	public boolean isSensor() {
		return sensor;
	}
	
	public Map<String, Class<? extends IAction>> getSupportedActions() {
		return supportedActions;
	}
	
	public Map<String, Class<? extends IEvent>> getSupportedEvents() {
		return supportedEvents;
	}
}
