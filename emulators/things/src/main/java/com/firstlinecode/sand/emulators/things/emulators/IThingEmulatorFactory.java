package com.firstlinecode.sand.emulators.things.emulators;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
}
