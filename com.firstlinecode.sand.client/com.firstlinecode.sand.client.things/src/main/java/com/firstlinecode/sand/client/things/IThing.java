package com.firstlinecode.sand.client.things;

public interface IThing extends IDevice {
	String getType();
	String getMode();
	String getSoftwareVersion();
	String getHardwareVersion();
}
