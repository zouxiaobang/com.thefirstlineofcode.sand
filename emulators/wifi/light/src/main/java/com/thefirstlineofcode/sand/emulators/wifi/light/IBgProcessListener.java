package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public interface IBgProcessListener {
	void registered(DeviceIdentity identity);
	void connected(IChatClient chatClient);
	void disconnected();
}
