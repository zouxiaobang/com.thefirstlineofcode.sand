package com.thefirstlineofcode.sand.client.things;

public interface IEvent<S, E> {
	S getSource();
	Class<E> getEventType();
	E getEvent();
}
