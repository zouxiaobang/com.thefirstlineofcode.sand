package com.firstlinecode.sand.client.things;

public interface IDevice {
	String getDeviceId();
	int getBattery();
	void powerOn();
	void powerOff();
	boolean isPowered();
	void addDeviceListener(IDeviceListener listener);
	boolean removeDeviceListener(IDeviceListener listener);
}
