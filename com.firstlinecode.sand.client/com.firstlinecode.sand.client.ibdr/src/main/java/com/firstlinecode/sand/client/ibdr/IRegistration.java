package com.firstlinecode.sand.client.ibdr;

import com.firstlinecode.chalk.core.stream.INegotiationListener;
import com.firstlinecode.chalk.network.IConnectionListener;
import com.firstlinecode.sand.protocols.ibdr.DeviceIdentity;

public interface IRegistration {
	DeviceIdentity register(String deviceId) throws RegistrationException;
	void remove();
	void addConnectionListener(IConnectionListener listener);
	void removeConnectionListener(IConnectionListener listener);
	void addNegotiationListener(INegotiationListener listener);
	void removeNegotiationListener(INegotiationListener listener);
}
