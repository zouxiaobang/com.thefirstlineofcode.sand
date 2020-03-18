package com.firstlinecode.sand.server.framework.things;

public class ModeDescriptor {
	private boolean actuator;
	private boolean concentrator;
	private boolean sensor;
	
	private Class<?>[] actionTypes;
	private Class<?>[] eventTypes;
	
	public ModeDescriptor() {}
	
	public ModeDescriptor(boolean concentrator) {
		this(true, null, null);
	}
	
	public ModeDescriptor(Class<?>[] actionTypes, Class<?>[] eventTypes) {
		this(false, actionTypes, eventTypes);
	}
	
	public ModeDescriptor(boolean concentrator, Class<?>[] actionTypes, Class<?>[] eventTypes) {
		this.concentrator = concentrator;
		this.actionTypes = actionTypes;
		this.eventTypes = eventTypes;
		
		if (actionTypes != null) {
			actuator = true;
		}
		
		if (eventTypes != null) {
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
	
	public Class<?>[] getActionTypes() {
		return actionTypes;
	}
	
	public void setActionTypes(Class<?>[] actionTypes) {
		this.actionTypes = actionTypes;
	}
	
	public Class<?>[] getEventTypes() {
		return eventTypes;
	}
	
	public void setEventTypes(Class<?>[] eventTypes) {
		this.eventTypes = eventTypes;
	}
}
