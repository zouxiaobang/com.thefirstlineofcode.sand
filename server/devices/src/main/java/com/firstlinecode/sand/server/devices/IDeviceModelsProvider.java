package com.firstlinecode.sand.server.devices;

import java.util.Map;

import org.pf4j.ExtensionPoint;

import com.firstlinecode.sand.protocols.core.ModelDescriptor;

public interface IDeviceModelsProvider extends ExtensionPoint {
	Map<String, ModelDescriptor> provide();
}
