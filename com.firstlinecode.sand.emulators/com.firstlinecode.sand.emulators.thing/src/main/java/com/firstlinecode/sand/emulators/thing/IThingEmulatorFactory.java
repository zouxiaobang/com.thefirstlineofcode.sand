package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.ICommunicationChip;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
	T create(ICommunicationChip<?> chip);
}
