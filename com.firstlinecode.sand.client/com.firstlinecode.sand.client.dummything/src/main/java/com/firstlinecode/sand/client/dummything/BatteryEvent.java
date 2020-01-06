package com.firstlinecode.sand.client.dummything;

public class BatteryEvent {
	private IDummyThing source;
	private int oldBattery;
	private int newBattery;
	
	public BatteryEvent(IDummyThing source, int oldBattery, int newBattery) {
		this.source = source;
		this.oldBattery = oldBattery;
		this.newBattery = newBattery;
	}

	public IDummyThing getSource() {
		return source;
	}
	
	public int getOldBattery() {
		return oldBattery;
	}

	public int getNewBattery() {
		return newBattery;
	}

}
