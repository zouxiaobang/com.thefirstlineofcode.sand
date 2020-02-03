package com.firstlinecode.sand.client.things;

public interface IActionListener<T> {
	void done(T result);
	void timeout();
}
