package com.thefirstlineofcode.sand.emulators.things.emulators;

import com.thefirstlineofcode.sand.client.things.IThing;
import com.thefirstlineofcode.sand.emulators.things.ui.AbstractThingEmulatorPanel;

public interface IThingEmulator extends IThing {
	void setDeviceId(String deviceId);
	void setBatteryPower(int batteryPower) ;
	void reset();
	AbstractThingEmulatorPanel<?> getPanel();
	String getThingStatus();
}
