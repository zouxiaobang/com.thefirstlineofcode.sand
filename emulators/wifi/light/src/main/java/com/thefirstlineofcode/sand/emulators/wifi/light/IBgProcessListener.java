package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public interface IBgProcessListener {
	void registered(DeviceIdentity identity);
	void registerExceptionOccurred(RegistrationException e);
	void failedToAuth();
	void FailedToConnect(ConnectionException e);
	void connectionExceptionOccurred(ConnectionException e);
	void connected(IChatClient chatClient);
	void disconnected();
}
