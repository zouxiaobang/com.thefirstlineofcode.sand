package com.firstlinecode.sand.client.things;

import java.util.Map;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public interface IThing extends IDevice {
	ICommunicator<?, ?, ?> getCommunicator();
	void configure(String key, Object value);
	Map<String, Object> getConfiguration();
	void addThingListener(IThingListener listener);
	boolean removeThingListener(IThingListener listener);
}
