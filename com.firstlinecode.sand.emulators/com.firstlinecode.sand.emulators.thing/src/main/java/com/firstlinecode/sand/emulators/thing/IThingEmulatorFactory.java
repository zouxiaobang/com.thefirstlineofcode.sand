package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.emulators.lora.LoraCommunicator;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
	T create(LoraCommunicator communicator);
}
