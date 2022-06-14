package com.thefirstlineofcode.sand.client.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDevice implements IDevice {
	protected static final String ATTRIBUTE_NAME_DEVICE_ID = "device_id";
	
	protected String deviceId;
	protected String type;
	protected String model;
	protected String name;
	protected boolean powered;
	protected int batteryPower;
	
	protected Map<String, String> attributes;
	protected boolean attributesChanged;
	
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
		
		attributes = loadDeviceAttributes();		
		if (attributes == null)
			attributes = new HashMap<>();
		
		attributesChanged = false;
		
		deviceId = getDeviceId(attributes);
		
		if (deviceId == null) {
			deviceId = generateDeviceId();
			
			if (deviceId == null)
				throw new RuntimeException("Failed to generate device ID. Null device ID.");
			
			attributes.put(ATTRIBUTE_NAME_DEVICE_ID, deviceId);
			attributesChanged = true;
		}
	}
	
	protected String getDeviceId(Map<String, String> attributes) {
		String deviceId = attributes.get(ATTRIBUTE_NAME_DEVICE_ID);
		
		return deviceId == null ? null : deviceId.trim();
	}
	
	protected abstract Map<String, String> loadDeviceAttributes();
	protected abstract String generateDeviceId();
	protected abstract void saveAttributes(Map<String, String> attributes);
	
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
