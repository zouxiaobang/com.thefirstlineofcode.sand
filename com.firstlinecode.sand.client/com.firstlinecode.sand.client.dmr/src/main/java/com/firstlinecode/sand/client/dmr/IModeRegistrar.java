package com.firstlinecode.sand.client.dmr;

import com.firstlinecode.sand.protocols.core.ModeDescriptor;

public interface IModeRegistrar {
	void registerModeDescriptor(ModeDescriptor modeDescriptor);
	boolean isActionNameSupported(String mode, String actionName);
	Class<?> getActionType(String mode, String actionName);
	boolean isEventNameSupported(String mode, String eventName);
	Class<?> getEventType(String mode, String eventName);
}
