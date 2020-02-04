package com.firstlinecode.sand.client.things;

import java.util.Map;

public interface IThing extends IDevice {
	String getSoftwareVersion();
	String getHardwareVersion();
	ICommunicationChip<?> getCommunicationChip();
	void configure(String key, Object value);
	Map<String, Object> getConfiguration();
	void addThingListener(IThingListener listener);
	boolean removeThingListener(IThingListener listener);
}
