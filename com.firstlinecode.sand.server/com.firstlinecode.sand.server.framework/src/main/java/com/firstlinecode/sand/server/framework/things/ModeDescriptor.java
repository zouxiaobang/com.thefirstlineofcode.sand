package com.firstlinecode.sand.server.framework.things;

public class ModeDescriptor {
	private boolean actuator;
	private boolean concentrator;
	private boolean sensor;
	
	private String[] actionTypes;
	private String[] eventTypes;
	
	public ModeDescriptor() {}
	
	public ModeDescriptor(boolean concentrator) {
		this(true, null, null);
	}
	
	public ModeDescriptor(String[] actionTypes, String[] eventTypes) {
		this(false, actionTypes, eventTypes);
	}
	
	public ModeDescriptor(boolean concentrator, String[] actionTypes, String[] eventTypes) {
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
	
	public String[] getActionTypes() {
		return actionTypes;
	}
	
	public void setActionTypes(String[] actionTypes) {
		this.actionTypes = actionTypes;
	}
	
	public String[] getEventTypes() {
		return eventTypes;
	}
	
	public void setEventTypes(String[] eventTypes) {
		this.eventTypes = eventTypes;
	}
}
