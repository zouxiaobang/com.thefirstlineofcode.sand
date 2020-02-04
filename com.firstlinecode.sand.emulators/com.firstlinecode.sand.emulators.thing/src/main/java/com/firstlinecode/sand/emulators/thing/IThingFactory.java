package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.ICommunicationChip;

public interface IThingFactory<IThing> {
	String getThingName();
	IThing create(ICommunicationChip<?> chip);
}
