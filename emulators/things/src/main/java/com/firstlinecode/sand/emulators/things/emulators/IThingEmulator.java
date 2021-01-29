package com.firstlinecode.sand.emulators.things.emulators;

import java.io.Externalizable;

import com.firstlinecode.sand.client.things.IThing;
import com.firstlinecode.sand.emulators.things.PowerEvent;
import com.firstlinecode.sand.emulators.things.ui.AbstractThingEmulatorPanel;

public interface IThingEmulator extends IThing, Externalizable {
	void setDeviceId(String deviceId);
	void setBatteryPower(int batteryPower) ;
	void powerChanged(PowerEvent event);
	void reset();
	AbstractThingEmulatorPanel<?> getPanel();
	String getThingName();
	String getThingStatus();
}
