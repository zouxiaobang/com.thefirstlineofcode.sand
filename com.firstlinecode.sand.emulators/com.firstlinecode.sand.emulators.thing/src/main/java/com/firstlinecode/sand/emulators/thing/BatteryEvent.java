package com.firstlinecode.sand.emulators.thing;

public class BatteryEvent {
	private IThing source;
	private int oldBattery;
	private int newBattery;
	
	public BatteryEvent(IThing source, int oldBattery, int newBattery) {
		this.source = source;
		this.oldBattery = oldBattery;
		this.newBattery = newBattery;
	}

	public IThing getSource() {
		return source;
	}
	
	public int getOldBattery() {
		return oldBattery;
	}

	public int getNewBattery() {
		return newBattery;
	}

}
