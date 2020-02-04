package com.firstlinecode.sand.emulators.blub;

import com.firstlinecode.sand.client.things.ICommunicationChip;
import com.firstlinecode.sand.emulators.thing.IThingFactory;

public class BlubEmulatorFactory implements IThingFactory<Blub> {
	public static final String THING_NAME = "Blub Emulator-01";
	
	@Override
	public String getThingName() {
		return THING_NAME;
	}

	@Override
	public Blub create(ICommunicationChip<?> chip) {
		return new Blub(chip);
	}
	
	

}
