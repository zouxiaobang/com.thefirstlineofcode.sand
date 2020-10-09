package com.firstlinecode.sand.emulators.lora.light;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.lora.network.LoraCommunicator;
import com.firstlinecode.sand.emulators.lora.things.AbstractLoraThingEmulatorFactory;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class LightEmulatorFactory extends AbstractLoraThingEmulatorFactory<Light> {
	public LightEmulatorFactory() {
		super(Light.THING_NAME, Light.THING_MODEL);
	}

	@Override
	public Light create(ICommunicator<LoraAddress, LoraAddress, byte[]> communicator) {
		if (communicator instanceof LoraCommunicator) {			
			return new Light((LoraCommunicator)communicator);
		}
		
		throw new RuntimeException("Lora light needs a lora Communicator.");
	}
}
