package com.firstlinecode.sand.client.dummyblub;

public interface IBlub {
	public enum SwitchState {
		ON,
		OFF,
		CONTROL
	}
	
	public enum BlubState {
		ON,
		OFF
	}
	
	SwitchState getSwitchState();
	BlubState getBlubState();
	void turnOn() throws NotRemoteControlStateException;
	void turnOff() throws NotRemoteControlStateException;
	void flash() throws NotRemoteControlStateException, NotTurnOffStateException;
}
