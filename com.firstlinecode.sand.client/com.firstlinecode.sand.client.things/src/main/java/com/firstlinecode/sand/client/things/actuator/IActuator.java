package com.firstlinecode.sand.client.things.actuator;

import com.firstlinecode.sand.client.things.IDevice;

public interface IActuator extends IDevice {
	void execute(IAction operation);
	void addActionListener(IActionListener<?> listener);
}
