package com.firstlinecode.sand.emulators.light;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.emulators.thing.ILight;
import com.firstlinecode.sand.emulators.thing.NotRemoteControlStateException;
import com.firstlinecode.sand.emulators.thing.NotTurnOffStateException;

public class Light implements IDevice, ILight {

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

	@Override
	public String getDeviceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSoftwareVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBatteryPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void powerOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void powerOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPowered() {
		// TODO Auto-generated method stub
		return false;
	}
}
