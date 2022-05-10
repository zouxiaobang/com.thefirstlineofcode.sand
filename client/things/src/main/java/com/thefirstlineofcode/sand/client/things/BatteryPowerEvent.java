package com.thefirstlineofcode.sand.client.things;

public class BatteryPowerEvent implements IEvent<IDevice, Integer> {
	private IDevice source;
	private Integer batteryPower;
	
	public BatteryPowerEvent(IDevice source, Integer batteryPower) {
		this.source = source;
		this.batteryPower = batteryPower;
	}
	
	@Override
	public IDevice getSource() {
		return source;
	}
	
	@Override
	public Class<Integer> getEventType() {
		return Integer.class;
	}
	
	@Override
	public Integer getEvent() {
		return batteryPower;
	}

}
