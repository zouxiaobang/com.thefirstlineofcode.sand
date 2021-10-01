package com.thefirstlineofcode.sand.client.dmr;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public class ModelRegistrar implements IModelRegistrar {
	private Map<String, ModelDescriptor> modelDescriptors = new HashMap<>();

	@Override
	public void registerModeDescriptor(ModelDescriptor modelDescriptor) {
		if (modelDescriptors.containsKey(modelDescriptor.getName())) {
			throw new IllegalArgumentException(String.format("Reduplicate model: '%s'.", modelDescriptor.getName()));
		}
		
		modelDescriptors.put(modelDescriptor.getName(), modelDescriptor);
	}

	@Override
	public boolean isActionSupported(String model, Protocol protocol) {
		if (!modelDescriptors.containsKey(model))
			throw new IllegalArgumentException(String.format("Unsupported model: '%s'", model));
		
		return modelDescriptors.get(model).getSupportedActions().containsKey(protocol);
	}

	@Override
	public Class<?> getActionType(String model, Protocol protocol) {
		if (!modelDescriptors.containsKey(model))
			throw new IllegalArgumentException(String.format("Unsupported model: '%s'", model));
		
		if (!modelDescriptors.get(model).getSupportedActions().containsKey(protocol)) {			
			throw new IllegalArgumentException(String.format("Unsupported action which's protocol is '%s' for device which's model is '%s'.", protocol, model));
		}
		
		return modelDescriptors.get(model).getSupportedActions().get(protocol);
	}

	@Override
	public boolean isEventSupported(String model, Protocol protocol) {
		if (!modelDescriptors.containsKey(model))
			throw new IllegalArgumentException(String.format("Unsupported model: '%s'", model));
		
		return modelDescriptors.get(model).getSupportedEvents().containsKey(protocol);
	}

	@Override
	public Class<?> getEventType(String model, Protocol protocol) {
		if (!modelDescriptors.containsKey(model))
			throw new IllegalArgumentException(String.format("Unsupported model: '%s'", model));
		
		if (!modelDescriptors.get(model).getSupportedActions().containsKey(protocol)) {			
			throw new IllegalArgumentException(String.format("Unsupported event which's protocol is '%s' for device which's model is '%s'.", protocol, model));
		}
		
		return modelDescriptors.get(model).getSupportedEvents().get(protocol);
	}

	@Override
	public boolean isActionSupported(String model, Class<?> actionType) {
		if (!modelDescriptors.containsKey(model))
			throw new IllegalArgumentException(String.format("Unsupported model: '%s'", model));
		
		return modelDescriptors.get(model).getSupportedActions().containsValue(actionType);
	}

	@Override
	public boolean isEventSupported(String model, Class<?> eventType) {
		if (!modelDescriptors.containsKey(model))
			throw new IllegalArgumentException(String.format("Unsupported model: '%s'", model));
		
		return modelDescriptors.get(model).getSupportedEvents().containsValue(eventType);
	}

	@Override
	public ModelDescriptor[] getModelDescriptors() {
		return modelDescriptors.values().toArray(new ModelDescriptor[modelDescriptors.size()]);
	}

}
