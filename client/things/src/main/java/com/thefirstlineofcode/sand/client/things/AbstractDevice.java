package com.thefirstlineofcode.sand.client.things;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDevice implements IDevice {
	protected String deviceId;
	protected String type;
	protected String model;
	protected String name;
	protected boolean powered;
	protected int batteryPower;
	
	protected List<IDeviceListener> listeners;
	
	public AbstractDevice() {}
	
	public AbstractDevice(String type, String model) {
		if (type == null || model == null)
			throw new IllegalArgumentException("Null type or model.");
		
		this.type = type;
		this.model = model;
		
		name = type + " - " + model;
		powered = false;
		batteryPower = 0;
		
		listeners =  new ArrayList<>();
		
		loadDeviceAttributes();
		
		if (deviceId == null) {
			deviceId = generateDeviceId();
			saveDeviceId(deviceId);
		}
	}
	
	protected abstract void loadDeviceAttributes();
	protected abstract String generateDeviceId();
	protected abstract void saveDeviceId(String deviceId);
	
	@Override
	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public String getDeviceType() {
		return type;
	}

	@Override
	public String getDeviceModel() {
		return model;
	}

	@Override
	public String getDeviceName() {
		return name;
	}
	
	@Override
	public void addDeviceListener(IDeviceListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public boolean removeDeviceListener(IDeviceListener listener) {
		return listeners.remove(listener);
	}
	
	@Override
	public boolean isPowered() {
		return powered && getBatteryPower() != 0;
	}
	
	@Override
	public int getBatteryPower() {
		return batteryPower;
	}

}
