package com.firstlinecode.sand.emulators.light;

import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorFactory;

public class LightEmulatorFactory extends AbstractThingEmulatorFactory<Light> {
	public LightEmulatorFactory() {
		super(Light.THING_NAME, Light.THING_MODE);
	}

	@Override
	public Light create(LoraCommunicator communicator) {
		return new Light(communicator);
	}

	@Override
	protected String getFullTypeName() {
		return "Light Emulator";
	}
}
