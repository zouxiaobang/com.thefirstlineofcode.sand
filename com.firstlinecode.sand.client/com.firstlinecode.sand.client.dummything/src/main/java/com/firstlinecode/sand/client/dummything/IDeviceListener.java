package com.firstlinecode.sand.client.dummything;

public interface IDeviceListener {
	void powerChanged(PowerEvent event);
	void batteryChanged(BatteryEvent event);
}
