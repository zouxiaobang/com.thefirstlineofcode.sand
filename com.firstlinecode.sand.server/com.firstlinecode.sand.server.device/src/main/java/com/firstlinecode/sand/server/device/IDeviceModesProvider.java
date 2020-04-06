package com.firstlinecode.sand.server.device;

import java.util.Map;

import com.firstlinecode.sand.protocols.core.ModeDescriptor;

public interface IDeviceModesProvider {
	Map<String, ModeDescriptor> provide();
}
