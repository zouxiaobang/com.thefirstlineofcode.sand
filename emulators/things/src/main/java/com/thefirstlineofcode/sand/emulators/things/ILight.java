package com.thefirstlineofcode.sand.emulators.things;

import com.thefirstlineofcode.sand.client.things.IDevice;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;

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
	
	public static final String ERROR_CODE_NOT_REMOTE_CONTROL_STATE = "01";
	
	SwitchState getSwitchState();
	LightState getLightState();
	void turnOn() throws ExecutionException;
	void turnOff() throws ExecutionException;
	void flash(int repeat) throws ExecutionException;
}
