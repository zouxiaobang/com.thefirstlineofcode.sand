package com.thefirstlineofcode.sand.client.core;

public interface IEvent<S, E> {
	S getSource();
	Class<E> getEventType();
	E getEvent();
}
