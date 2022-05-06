package com.thefirstlineofcode.sand.emulators.commons;

public abstract class AbstractThingEmulatorFactory<T extends IThingEmulator> implements IThingEmulatorFactory<T> {
	protected String thingTypeName;
	protected String thingFullTypeName;
	
	public AbstractThingEmulatorFactory(String deviceType, String deviceMode) {
		thingTypeName = deviceType + " - " + deviceMode;
	}
	
	@Override
	public String getThingName() {
		return thingTypeName;
	}

}
