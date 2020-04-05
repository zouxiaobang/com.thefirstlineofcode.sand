package com.firstlinecode.sand.emulators.thing;

public abstract class AbstractThingEmulatorFactory<T extends IThingEmulator> implements IThingEmulatorFactory<T> {
	protected String tingTypeName;
	protected String thingFullTypeName;
	
	public AbstractThingEmulatorFactory(String deviceType, String deviceMode) {
		tingTypeName = deviceType + " - " + deviceMode;
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
