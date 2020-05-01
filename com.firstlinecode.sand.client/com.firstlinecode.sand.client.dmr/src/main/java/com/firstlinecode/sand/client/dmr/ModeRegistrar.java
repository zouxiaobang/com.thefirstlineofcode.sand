package com.firstlinecode.sand.client.dmr;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.sand.protocols.core.ModeDescriptor;

public class ModeRegistrar implements IModeRegistrar {
	private Map<String, ModeDescriptor> modeDescriptors = new HashMap<String, ModeDescriptor>();

	@Override
	public void registerModeDescriptor(ModeDescriptor modeDescriptor) {
		if (modeDescriptors.containsKey(modeDescriptor.getName())) {
			throw new IllegalArgumentException(String.format("Reduplicate mode: '%s'.", modeDescriptor.getName()));
		}
		
		modeDescriptors.put(modeDescriptor.getName(), modeDescriptor);
	}

	@Override
	public boolean isActionNameSupported(String mode, String actionName) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		return modeDescriptors.get(mode).getSupportedActions().containsKey(actionName);
	}

	@Override
	public Class<?> getActionType(String mode, String actionName) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		if (!modeDescriptors.get(mode).getSupportedActions().containsKey(actionName)) {			
			throw new IllegalArgumentException(String.format("Unsupported action name '%s' for device which's mode is '%s'.", actionName, mode));
		}
		
		return modeDescriptors.get(mode).getSupportedActions().get(actionName);
	}

	@Override
	public boolean isEventNameSupported(String mode, String eventName) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		return modeDescriptors.get(mode).getSupportedEvents().containsKey(eventName);
	}

	@Override
	public Class<?> getEventType(String mode, String eventName) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		if (!modeDescriptors.get(mode).getSupportedActions().containsKey(eventName)) {			
			throw new IllegalArgumentException(String.format("Unsupported event name '%s' for device which's mode is '%s'.", eventName, mode));
		}
		
		return modeDescriptors.get(mode).getSupportedEvents().get(eventName);
	}

}
