package com.thefirstlineofcode.sand.protocols.core;

import java.util.Collections;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;

public class ModelDescriptor {
	private String name;
	private boolean actuator;
	private boolean concentrator;
	private boolean sensor;
	
	private Map<Protocol, Class<?>> supportedActions;
	private Map<Protocol, Class<?>> supportedEvents;
	
	public ModelDescriptor(String name) {
		this(name, false);
	}
	
	public ModelDescriptor(String name, boolean concentrator) {
		this(name, concentrator, null, null);
	}
	
	public ModelDescriptor(String name, Map<Protocol, Class<?>> suppportedActions, Map<Protocol, Class<?>> supportedEvents) {
		this(name, false, suppportedActions, supportedEvents);
	}
	
	public ModelDescriptor(String name, boolean concentrator, Map<Protocol, Class<?>> suppportedActions, Map<Protocol, Class<?>> supportedEvents) {
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
	
	public Map<Protocol, Class<?>> getSupportedActions() {
		return supportedActions == null ? createEmptyMap() : Collections.unmodifiableMap(supportedActions);
	}

	private Map<Protocol, Class<?>> createEmptyMap() {
		return Collections.emptyMap();
	}
	
	public Map<Protocol, Class<?>> getSupportedEvents() {
		return supportedEvents == null ? createEmptyMap() : Collections.unmodifiableMap(supportedEvents);
	}
	
	public int guessLanExecutionTimeout(Object action) {
		for (Class<?> actionType : supportedActions.values()) {
			if (actionType.equals(action.getClass())) {
				return calculateLanExecutionTimeout(action);
			}
		}
		
		throw new IllegalArgumentException(String.format("'%s' isn't an supported action.", action.getClass().getName()));
	}

	protected int calculateLanExecutionTimeout(Object action) {
		throw new RuntimeException(String.format("Calculate LAN execution timeout for action '%s' not supported.",
				action.getClass().getName()), new OperationNotSupportedException());
	}
}
