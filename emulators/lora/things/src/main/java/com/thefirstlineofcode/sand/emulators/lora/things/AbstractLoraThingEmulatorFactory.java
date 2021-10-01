package com.thefirstlineofcode.sand.emulators.lora.things;

import com.thefirstlineofcode.sand.emulators.things.emulators.AbstractThingEmulatorFactory;
import com.thefirstlineofcode.sand.emulators.things.emulators.ICommunicationNetworkThingEmulatorFactory;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public abstract class AbstractLoraThingEmulatorFactory<T extends AbstractLoraThingEmulator> extends AbstractThingEmulatorFactory<T>
		implements ICommunicationNetworkThingEmulatorFactory<LoraAddress, LoraAddress, byte[], T> {	
	public AbstractLoraThingEmulatorFactory(String deviceType, String deviceMode) {
		super(deviceType, deviceMode);
	}

}
