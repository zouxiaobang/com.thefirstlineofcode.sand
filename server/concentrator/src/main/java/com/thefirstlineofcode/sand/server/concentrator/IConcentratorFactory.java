package com.thefirstlineofcode.sand.server.concentrator;

import com.thefirstlineofcode.sand.server.devices.Device;

public interface IConcentratorFactory {
	boolean isConcentrator(Device device);
	IConcentrator getConcentrator(Device device);
}
