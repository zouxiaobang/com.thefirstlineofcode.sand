package com.firstlinecode.sand.client.things;

import java.util.Map;

public interface IDevice {
	String getDeviceId();
	int getBatteryPower();
	void powerOn();
	void powerOff();
	boolean isPowered();
	void configure(String key, Object value);
	Map<String, Object> getConfiguration();
	void addDeviceListener(IDeviceListener listener);
	boolean removeDeviceListener(IDeviceListener listener);
}
