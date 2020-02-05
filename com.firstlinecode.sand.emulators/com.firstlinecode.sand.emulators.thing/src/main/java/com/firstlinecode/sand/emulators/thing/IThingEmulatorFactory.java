package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
	T create(ICommunicationChip<?> chip);
}
