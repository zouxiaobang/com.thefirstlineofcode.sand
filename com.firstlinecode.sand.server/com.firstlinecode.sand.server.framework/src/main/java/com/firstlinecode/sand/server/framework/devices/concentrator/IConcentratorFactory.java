package com.firstlinecode.sand.server.framework.devices.concentrator;

import com.firstlinecode.sand.server.framework.devices.Device;

public interface IConcentratorFactory {
	boolean isConcentrator(Device device);
	IConcentrator getConcentrator(Device device);
}
