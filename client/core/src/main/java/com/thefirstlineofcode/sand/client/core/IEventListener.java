package com.thefirstlineofcode.sand.client.core;

public interface IEventListener {
	Class<? extends IEvent<?, ?>>[] getInterests();
	<S, E> void occurred(IEvent<S, E> event);
}
