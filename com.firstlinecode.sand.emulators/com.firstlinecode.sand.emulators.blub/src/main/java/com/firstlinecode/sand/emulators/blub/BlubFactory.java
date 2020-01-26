package com.firstlinecode.sand.emulators.blub;

import com.firstlinecode.sand.emulators.lora.ILoraChip;
import com.firstlinecode.sand.emulators.thing.IThingFactory;

public class BlubFactory implements IThingFactory<Blub> {
	public static final String THING_NAME = "Blub";
	
	@Override
	public String getThingName() {
		return THING_NAME;
	}
	
	@Override
	public Blub create(ILoraChip chip) {
		return new Blub(chip);
	}

}
