package com.firstlinecode.sand.server.device;

import java.util.Map;

import com.firstlinecode.sand.protocols.core.ModelDescriptor;

public interface IDeviceModelsProvider {
	Map<String, ModelDescriptor> provide();
}
