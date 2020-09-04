package com.firstlinecode.sand.emulators.things;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
}
