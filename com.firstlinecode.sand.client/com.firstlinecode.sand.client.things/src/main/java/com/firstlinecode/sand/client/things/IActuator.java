package com.firstlinecode.sand.client.things;

public interface IActuator extends IDevice {
	void execute(IAction operation);
	void addActionListener(IActionListener<?> listener);
}
