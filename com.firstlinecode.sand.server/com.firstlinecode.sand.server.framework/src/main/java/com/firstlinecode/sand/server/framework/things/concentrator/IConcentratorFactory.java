package com.firstlinecode.sand.server.framework.things.concentrator;

import com.firstlinecode.sand.server.framework.things.Device;

public interface IConcentratorFactory {
	boolean isConcentrator(Device device);
	IConcentrator getConcentrator(Device device);
}
