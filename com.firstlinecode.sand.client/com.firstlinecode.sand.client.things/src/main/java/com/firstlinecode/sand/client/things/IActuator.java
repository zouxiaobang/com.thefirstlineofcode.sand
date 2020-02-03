package com.firstlinecode.sand.client.things;

public interface IActuator extends IThing {
	void execute(IAction operation);
	void addActionListener(IActionListener<?> listener);
}
