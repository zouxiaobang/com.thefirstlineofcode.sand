package com.firstlinecode.sand.emulators.thing;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
}
