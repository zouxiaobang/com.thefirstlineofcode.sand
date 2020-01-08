package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.basalt.protocol.core.JabberId;

public interface IDeviceRegistrar {
	JabberId register(String deviceId);
	void remove(String deviceId);
}
