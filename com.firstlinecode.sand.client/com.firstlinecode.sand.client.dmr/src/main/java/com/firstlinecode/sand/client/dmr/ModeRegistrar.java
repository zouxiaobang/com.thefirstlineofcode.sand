package com.firstlinecode.sand.client.dmr;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;

public class ModeRegistrar implements IModeRegistrar {
	private Map<String, ModeDescriptor> modeDescriptors = new HashMap<>();

	@Override
	public void registerModeDescriptor(ModeDescriptor modeDescriptor) {
		if (modeDescriptors.containsKey(modeDescriptor.getName())) {
			throw new IllegalArgumentException(String.format("Reduplicate mode: '%s'.", modeDescriptor.getName()));
		}
		
		modeDescriptors.put(modeDescriptor.getName(), modeDescriptor);
	}

	@Override
	public boolean isActionSupported(String mode, Protocol protocol) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		return modeDescriptors.get(mode).getSupportedActions().containsKey(protocol);
	}

	@Override
	public Class<?> getActionType(String mode, Protocol protocol) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		if (!modeDescriptors.get(mode).getSupportedActions().containsKey(protocol)) {			
			throw new IllegalArgumentException(String.format("Unsupported action which's protocol is '%s' for device which's mode is '%s'.", protocol, mode));
		}
		
		return modeDescriptors.get(mode).getSupportedActions().get(protocol);
	}

	@Override
	public boolean isEventSupported(String mode, Protocol protocol) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		return modeDescriptors.get(mode).getSupportedEvents().containsKey(protocol);
	}

	@Override
	public Class<?> getEventType(String mode, Protocol protocol) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		if (!modeDescriptors.get(mode).getSupportedActions().containsKey(protocol)) {			
			throw new IllegalArgumentException(String.format("Unsupported event which's protocol is '%s' for device which's mode is '%s'.", protocol, mode));
		}
		
		return modeDescriptors.get(mode).getSupportedEvents().get(protocol);
	}

	@Override
	public boolean isActionSupported(String mode, Class<?> actionType) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		return modeDescriptors.get(mode).getSupportedActions().containsValue(actionType);
	}

	@Override
	public boolean isEventSupported(String mode, Class<?> eventType) {
		if (!modeDescriptors.containsKey(mode))
			throw new IllegalArgumentException(String.format("Unsupported mode: '%s'", mode));
		
		return modeDescriptors.get(mode).getSupportedEvents().containsValue(eventType);
	}

}
