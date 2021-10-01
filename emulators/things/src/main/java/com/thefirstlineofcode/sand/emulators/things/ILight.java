package com.thefirstlineofcode.sand.emulators.things;

import com.thefirstlineofcode.sand.client.things.IDevice;

public interface ILight extends IDevice {
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
	void flash() throws NotRemoteControlStateException, NotTurnedOffStateException;
}
