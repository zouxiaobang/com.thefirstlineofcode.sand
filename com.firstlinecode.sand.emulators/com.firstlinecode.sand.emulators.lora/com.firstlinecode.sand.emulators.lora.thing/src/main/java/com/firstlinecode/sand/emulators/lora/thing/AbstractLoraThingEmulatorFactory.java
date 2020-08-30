package com.firstlinecode.sand.emulators.lora.thing;

import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorFactory;
import com.firstlinecode.sand.emulators.thing.ICommunicationNetworkThingEmulatorFactory;
import com.firstlinecode.sand.emulators.thing.IThingEmulator;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public abstract class AbstractLoraThingEmulatorFactory<T extends IThingEmulator> extends AbstractThingEmulatorFactory<T>
		implements ICommunicationNetworkThingEmulatorFactory<LoraAddress, LoraAddress, byte[], T> {
	protected String tingTypeName;
	protected String thingFullTypeName;
	
	public AbstractLoraThingEmulatorFactory(String deviceType, String deviceMode) {
		super(deviceType, deviceMode);
	}
	
	@Override
	public String getThingName() {
		return tingTypeName;
	}
	
	@Override
	public String getThingFullTypeName() {
		return getFullTypeName() + " " + tingTypeName;
	}

	protected abstract String getFullTypeName();

}
