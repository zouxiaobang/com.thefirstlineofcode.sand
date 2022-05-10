package com.thefirstlineofcode.sand.client.things;

public interface IDevice {
	String getDeviceId();
	String getDeviceType();
	String getDeviceModel();
	String getDeviceName();
	String getSoftwareVersion();
	int getBatteryPower();
	boolean isPowered();
	void addDeviceListener(IDeviceListener listener);
	boolean removeDeviceListener(IDeviceListener listener);
}
