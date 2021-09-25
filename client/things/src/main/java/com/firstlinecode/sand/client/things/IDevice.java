package com.firstlinecode.sand.client.things;

public interface IDevice {
	String getDeviceId();
	String getThingType();
	String getThingModel();
	String getThingName();
	String getSoftwareVersion();
	int getBatteryPower();
	void powerOn();
	void powerOff();
	boolean isPowered();
	void addDeviceListener(IDeviceListener listener);
	boolean removeDeviceListener(IDeviceListener listener);
}
