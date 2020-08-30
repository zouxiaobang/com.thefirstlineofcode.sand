package com.firstlinecode.sand.emulators.light;

import com.firstlinecode.sand.emulators.modes.ILight;
import com.firstlinecode.sand.emulators.modes.NotRemoteControlStateException;
import com.firstlinecode.sand.emulators.modes.NotTurnOffStateException;

public class Light implements ILight {

	@Override
	public SwitchState getSwitchState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LightState getLightState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void turnOn() throws NotRemoteControlStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnOff() throws NotRemoteControlStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flash() throws NotRemoteControlStateException, NotTurnOffStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getThingName() {
		// TODO Auto-generated method stub
		return null;
	}
}
