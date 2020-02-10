package com.firstlinecode.sand.emulators.blub;

import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorFactory;

public class BlubEmulatorFactory extends AbstractThingEmulatorFactory<Blub> {
	public BlubEmulatorFactory() {
		super(Blub.THING_TYPE, Blub.THING_MODE);
	}

	@Override
	public Blub create(LoraCommunicator communicator) {
		return new Blub(communicator);
	}
}
