package com.firstlinecode.sand.emulators.blub;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.commuication.ParamsMap;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorFactory;

public class BlubEmulatorFactory extends AbstractThingEmulatorFactory<Blub> {
	public BlubEmulatorFactory() {
		super(Blub.THING_TYPE, Blub.THING_MODE);
	}
	
	@Override
	public <P extends ParamsMap> Blub create(ICommunicator<?, ?> communicator, P params) {
		return new Blub(communicator);
	}
	
}
