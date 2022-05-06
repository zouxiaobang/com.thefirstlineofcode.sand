package com.thefirstlineofcode.sand.emulators.commons;

import com.thefirstlineofcode.sand.client.things.IThing;
import com.thefirstlineofcode.sand.emulators.commons.ui.AbstractThingEmulatorPanel;

public interface IThingEmulator extends IThing {
	void setDeviceId(String deviceId);
	void setBatteryPower(int batteryPower) ;
	void reset();
	AbstractThingEmulatorPanel<?> getPanel();
	String getThingStatus();
}
