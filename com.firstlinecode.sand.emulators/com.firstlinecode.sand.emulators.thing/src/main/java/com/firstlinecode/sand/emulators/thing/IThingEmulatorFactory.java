package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;

public interface IThingEmulatorFactory {
	String getThingName();
	IThingEmulator create(ICommunicationChip<?> chip);
}
