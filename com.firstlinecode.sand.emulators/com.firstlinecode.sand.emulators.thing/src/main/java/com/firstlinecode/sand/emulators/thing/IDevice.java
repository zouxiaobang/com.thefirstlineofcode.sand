package com.firstlinecode.sand.emulators.thing;

public interface IDevice {
	void setDeviceId(String deviceId);
	String getDeviceId();
	void powerOn();
	void powerOff();
	boolean isPowered();
	void setBattery(int battery);
	int getBattery();
	void addDeviceListener(IDeviceListener listener);
	boolean removeDeviceListener(IDeviceListener listener);
}
