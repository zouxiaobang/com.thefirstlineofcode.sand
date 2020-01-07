package com.firstlinecode.sand.emulators.thing;

public interface IThingFactory<T extends IThing> {
	String getThingName();
	T create();
}
