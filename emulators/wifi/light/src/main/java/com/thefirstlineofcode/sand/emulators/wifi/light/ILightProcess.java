package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public interface ILightProcess {
	void start();
	void start(DeviceIdentity identity);
	void stop();
	void setProcessListener(IProcessListener pocessListener);
	void removeProcessListener(IProcessListener pocessListener);
}
