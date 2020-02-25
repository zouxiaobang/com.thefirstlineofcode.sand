package com.firstlinecode.sand.client.things.concentrator;

public class Node<T> {
	private String deviceId;
	private T address;
	private boolean enabled;
	
	public Node(String deviceId, T address) {
		this.deviceId = deviceId;
		this.address = address;
		
		enabled = false;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public T getAddress() {
		return address;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setAddress(T address) {
		this.address = address;
	}
	
}
