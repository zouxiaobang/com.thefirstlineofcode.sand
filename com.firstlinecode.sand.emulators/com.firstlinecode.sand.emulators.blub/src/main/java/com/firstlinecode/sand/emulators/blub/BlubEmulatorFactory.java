package com.firstlinecode.sand.emulators.blub;

import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorFactory;

public class BlubEmulatorFactory extends AbstractThingEmulatorFactory<Blub> {
	
	public BlubEmulatorFactory() {
		super(Blub.THING_TYPE, Blub.THING_MODE);
	}
	
	@Override
	public Blub create(ICommunicationChip<?> chip) {
		return new Blub(chip);
	}
	
}
