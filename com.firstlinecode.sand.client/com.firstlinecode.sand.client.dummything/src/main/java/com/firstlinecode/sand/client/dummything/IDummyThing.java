package com.firstlinecode.sand.client.dummything;

import java.io.Externalizable;

public interface IDummyThing extends IDummyDevice, Externalizable {
	void setName(String name);
	String getName();
	void reset();
	AbstractDummyThingPanel getPanel();
}
