package com.thefirstlineofcode.sand.emulators.commons;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
}
