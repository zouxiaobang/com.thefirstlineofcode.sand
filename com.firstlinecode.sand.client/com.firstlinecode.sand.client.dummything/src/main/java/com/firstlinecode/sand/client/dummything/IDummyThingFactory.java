package com.firstlinecode.sand.client.dummything;

public interface IDummyThingFactory<T extends IDummyThing> {
	String getThingName();
	T create();
}
