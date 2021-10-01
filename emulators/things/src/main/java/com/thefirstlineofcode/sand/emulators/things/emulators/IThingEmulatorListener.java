package com.thefirstlineofcode.sand.emulators.things.emulators;

import com.thefirstlineofcode.sand.client.things.IDeviceListener;
import com.thefirstlineofcode.sand.emulators.things.PowerEvent;

public interface IThingEmulatorListener extends IDeviceListener {
	void powerChanged(PowerEvent event);
}
