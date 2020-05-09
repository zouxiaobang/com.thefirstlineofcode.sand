package com.firstlinecode.sand.client.dmr;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;

public interface IModeRegistrar {
	void registerModeDescriptor(ModeDescriptor modeDescriptor);
	boolean isActionSupported(String mode, Protocol protocol);
	boolean isActionSupported(String mode, Class<?> actionType);
	Class<?> getActionType(String mode, Protocol protocol);
	boolean isEventSupported(String mode, Protocol protocol);
	boolean isEventSupported(String mode, Class<?> eventType);
	Class<?> getEventType(String mode, Protocol protocol);
}
