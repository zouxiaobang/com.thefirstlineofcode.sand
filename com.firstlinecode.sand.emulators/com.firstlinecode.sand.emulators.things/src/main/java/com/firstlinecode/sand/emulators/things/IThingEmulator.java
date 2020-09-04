package com.firstlinecode.sand.emulators.things;

import java.io.Externalizable;

import com.firstlinecode.sand.client.things.IThing;

public interface IThingEmulator extends IThing, Externalizable {
	void setDeviceId(String deviceId);
	void setBatteryPower(int batteryPower) ;
	void powerChanged(PowerEvent event);
	void reset();
	AbstractThingEmulatorPanel getPanel();
	String getThingName();
	String getThingStatus();
}
