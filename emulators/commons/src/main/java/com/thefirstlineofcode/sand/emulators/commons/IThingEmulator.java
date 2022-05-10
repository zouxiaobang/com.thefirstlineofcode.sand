package com.thefirstlineofcode.sand.emulators.commons;

import com.thefirstlineofcode.sand.client.things.IDevice;
import com.thefirstlineofcode.sand.emulators.commons.ui.AbstractThingEmulatorPanel;

public interface IThingEmulator extends IDevice {
	void powerOn();
	void powerOff();
	void reset();
	AbstractThingEmulatorPanel<?> getPanel();
	String getThingStatus();
}
