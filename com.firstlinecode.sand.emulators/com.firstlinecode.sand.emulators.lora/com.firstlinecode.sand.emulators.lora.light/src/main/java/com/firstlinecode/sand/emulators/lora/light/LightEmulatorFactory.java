package com.firstlinecode.sand.emulators.lora.light;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.lora.network.LoraCommunicator;
import com.firstlinecode.sand.emulators.lora.thing.AbstractLoraThingEmulatorFactory;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class LightEmulatorFactory extends AbstractLoraThingEmulatorFactory<Light> {
	public LightEmulatorFactory() {
		super(Light.THING_NAME, Light.THING_MODE);
	}

	@Override
	public Light create(ICommunicator<LoraAddress, LoraAddress, byte[]> communicator) {
		if (communicator instanceof LoraCommunicator) {			
			return new Light((LoraCommunicator)communicator);
		}
		
		throw new RuntimeException("Light needs a lora Communicator.");
	}

	@Override
	protected String getFullTypeName() {
		return "Light Emulator";
	}
}
