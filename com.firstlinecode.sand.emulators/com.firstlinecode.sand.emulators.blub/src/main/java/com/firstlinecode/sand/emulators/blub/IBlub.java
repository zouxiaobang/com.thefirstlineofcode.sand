package com.firstlinecode.sand.emulators.blub;

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
