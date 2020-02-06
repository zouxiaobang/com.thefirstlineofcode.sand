package com.firstlinecode.sand.emulators.thing;

public abstract class AbstractThingEmulatorFactory<T extends IThingEmulator> implements IThingEmulatorFactory<T> {
	protected String tingName;
	
	public AbstractThingEmulatorFactory(String deviceType, String deviceMode) {
		tingName = deviceType + " - " + deviceMode;
	}
	
	@Override
	public String getThingName() {
		return tingName;
	}

}
