package com.thefirstlineofcode.sand.client.dmr;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public interface IModelRegistrar {
	void registerModeDescriptor(ModelDescriptor modelDescriptor);
	ModelDescriptor[] getModelDescriptors();
	boolean isActionSupported(String model, Protocol protocol);
	boolean isActionSupported(String model, Class<?> actionType);
	Class<?> getActionType(String model, Protocol protocol);
	boolean isEventSupported(String model, Protocol protocol);
	boolean isEventSupported(String model, Class<?> eventType);
	Class<?> getEventType(String model, Protocol protocol);
}
