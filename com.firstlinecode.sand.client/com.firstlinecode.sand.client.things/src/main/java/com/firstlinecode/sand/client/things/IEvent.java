package com.firstlinecode.sand.client.things;

public interface IEvent<K, V> {
	K getSource();
	Class<V> getEventType();
	V getEvent();
}
