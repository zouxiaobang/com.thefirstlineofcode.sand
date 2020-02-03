package com.firstlinecode.sand.client.things;

public interface IEventListener {
	Class<? extends IEvent<?, ?>>[] getInterests();
	<K, V> void occurred(IEvent<K, V> event);
}
