package com.firstlinecode.sand.emulators.thing;

import java.io.Externalizable;

import com.firstlinecode.sand.emulators.lora.ILoraChip;

public interface IThing extends IDevice, Externalizable {
	String getName();
	ILoraChip getChip();
	void reset();
	AbstractThingPanel getPanel();
}
