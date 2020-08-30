package com.firstlinecode.sand.emulators.modes;

public interface ILight {
	public enum SwitchState {
		ON,
		OFF,
		CONTROL
	}
	
	public enum LightState {
		ON,
		OFF
	}
	
	SwitchState getSwitchState();
	LightState getLightState();
	void turnOn() throws NotRemoteControlStateException;
	void turnOff() throws NotRemoteControlStateException;
	void flash() throws NotRemoteControlStateException, NotTurnOffStateException;
	String getThingName();
}
