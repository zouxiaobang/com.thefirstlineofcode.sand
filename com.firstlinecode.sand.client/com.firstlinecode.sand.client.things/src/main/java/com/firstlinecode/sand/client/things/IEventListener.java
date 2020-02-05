package com.firstlinecode.sand.client.things;

public interface IEventListener {
	Class<? extends IEvent<?, ?>>[] getInterests();
	<S, E> void occurred(IEvent<S, E> event);
}
