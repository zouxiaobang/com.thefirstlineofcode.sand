package com.firstlinecode.sand.client.dummything;

public interface IDummyThingFactory<T extends IDummyThing> {
	String getThingName();
	String getThingsName();
	T create();
}
