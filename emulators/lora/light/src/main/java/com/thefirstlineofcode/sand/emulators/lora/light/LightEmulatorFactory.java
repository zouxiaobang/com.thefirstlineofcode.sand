package com.thefirstlineofcode.sand.emulators.lora.light;

import com.thefirstlineofcode.sand.client.core.commuication.ICommunicator;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraCommunicator;
import com.thefirstlineofcode.sand.emulators.lora.things.AbstractLoraThingEmulatorFactory;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class LightEmulatorFactory extends AbstractLoraThingEmulatorFactory<Light> {
	public LightEmulatorFactory() {
		super(Light.THING_TYPE, Light.THING_MODEL);
	}

	@Override
	public Light create(ICommunicator<LoraAddress, LoraAddress, byte[]> communicator) {
		if (communicator instanceof LoraCommunicator) {			
			return new Light((LoraCommunicator)communicator);
		}
		
		throw new RuntimeException("Lora light needs a lora Communicator.");
	}
}
