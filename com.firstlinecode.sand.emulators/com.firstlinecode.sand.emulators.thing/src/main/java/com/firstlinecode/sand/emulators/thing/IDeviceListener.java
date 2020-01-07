package com.firstlinecode.sand.emulators.thing;

public interface IDeviceListener {
	void powerChanged(PowerEvent event);
	void batteryChanged(BatteryEvent event);
}
