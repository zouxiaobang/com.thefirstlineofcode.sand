package com.firstlinecode.sand.emulators.thing;

public class BatteryEvent {
	private IThingEmulator source;
	private int oldBattery;
	private int newBattery;
	
	public BatteryEvent(IThingEmulator source, int oldBattery, int newBattery) {
		this.source = source;
		this.oldBattery = oldBattery;
		this.newBattery = newBattery;
	}

	public IThingEmulator getSource() {
		return source;
	}
	
	public int getOldBattery() {
		return oldBattery;
	}

	public int getNewBattery() {
		return newBattery;
	}

}
