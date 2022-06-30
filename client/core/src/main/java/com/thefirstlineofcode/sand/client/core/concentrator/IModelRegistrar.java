package com.thefirstlineofcode.sand.client.core.concentrator;

import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public interface IModelRegistrar {
	void registerModeDescriptor(ModelDescriptor modelDescriptor);
	ModelDescriptor[] getModelDescriptors();
	ModelDescriptor getModelDescriptor(String model);
	boolean isActionSupported(String model, Protocol protocol);
	boolean isActionSupported(String model, Class<?> actionType);
	Class<?> getActionType(String model, Protocol protocol);
	boolean isEventSupported(String model, Protocol protocol);
	boolean isEventSupported(String model, Class<?> eventType);
	Class<?> getEventType(String model, Protocol protocol);
}
