package com.firstlinecode.sand.emulators.things.emulators;

import com.firstlinecode.sand.client.things.IDeviceListener;
import com.firstlinecode.sand.emulators.things.PowerEvent;

public interface IThingEmulatorListener extends IDeviceListener {
	void powerChanged(PowerEvent event);
}
