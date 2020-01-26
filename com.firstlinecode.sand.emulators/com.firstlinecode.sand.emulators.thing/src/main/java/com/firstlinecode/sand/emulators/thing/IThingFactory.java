package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.emulators.lora.ILoraChip;

public interface IThingFactory<T extends IThing> {
	String getThingName();
	T create(ILoraChip chip);
}
