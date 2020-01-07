package com.firstlinecode.sand.emulators.thing;

import java.io.Externalizable;

public interface IThing extends IDevice, Externalizable {
	void setName(String name);
	String getName();
	void reset();
	AbstractThingPanel getPanel();
}
