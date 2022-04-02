package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public interface IProcessListener {
	public enum RegistrationError {
		CAN_NOT_CONNECT_TO_SERVER,
		NOT_AUTHORIZED,
		HAS_REGISTED_ALREADY
	}
	
	public enum ConnectError {
		CAN_NOT_CONNECT_TO_SERVER,
		NOT_REGISTED,
		HAS_CONNECTED_ALREADY
	}
	
	void registered(DeviceIdentity identity);
	void registerErrorOccurred(RegistrationError error);
	void connected(IChatClient chatClient);
	void connectErrorOccurred(ConnectError error);
	void unconnected();
	void messageReceived(String xml, byte[] binData);
	void messageSent(String xml, byte[] binData);
	void actionRequested(Protocol protocol, Object action);
}
