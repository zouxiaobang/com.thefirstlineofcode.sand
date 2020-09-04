package com.firstlinecode.sand.emulators.lora.thing;

import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorFactory;
import com.firstlinecode.sand.emulators.thing.ICommunicationNetworkThingEmulatorFactory;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public abstract class AbstractLoraThingEmulatorFactory<T extends AbstractLoraThingEmulator> extends AbstractThingEmulatorFactory<T>
		implements ICommunicationNetworkThingEmulatorFactory<LoraAddress, LoraAddress, byte[], T> {	
	public AbstractLoraThingEmulatorFactory(String deviceType, String deviceMode) {
		super(deviceType, deviceMode);
	}

}
