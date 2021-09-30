package com.firstlinecode.sand.client.dmr;

import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

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
