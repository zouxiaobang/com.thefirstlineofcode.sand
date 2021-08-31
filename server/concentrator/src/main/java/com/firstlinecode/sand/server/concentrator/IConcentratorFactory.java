package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.sand.server.devices.Device;

public interface IConcentratorFactory {
	boolean isConcentrator(Device device);
	IConcentrator getConcentrator(Device device);
}
