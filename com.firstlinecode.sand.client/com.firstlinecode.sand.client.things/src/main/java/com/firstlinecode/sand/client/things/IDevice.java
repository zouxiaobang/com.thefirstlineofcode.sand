package com.firstlinecode.sand.client.things;

public interface IDevice {
	String getDeviceId();
	String getMode();
	String getSoftwareVersion();
	int getBatteryPower();
	void powerOn();
	void powerOff();
	boolean isPowered();
}
