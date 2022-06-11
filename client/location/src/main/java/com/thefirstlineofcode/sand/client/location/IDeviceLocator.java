package com.thefirstlineofcode.sand.client.location;

import java.util.List;

import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.sand.protocols.location.DeviceLocation;

public interface IDeviceLocator {
	void locateDevices(List<String> deviceIds);
	void addListener(Listener listener);
	boolean removeListener(Listener listener);
	
	public interface Listener {
		void located(List<DeviceLocation> deviceLocations);
		void occurred(StanzaError error);
		void timeout();
	}
}
