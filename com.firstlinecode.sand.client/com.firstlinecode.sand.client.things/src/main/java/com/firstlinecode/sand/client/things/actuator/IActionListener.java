package com.firstlinecode.sand.client.things.actuator;

public interface IActionListener<T> {
	void done(T result);
	void timeout();
}
