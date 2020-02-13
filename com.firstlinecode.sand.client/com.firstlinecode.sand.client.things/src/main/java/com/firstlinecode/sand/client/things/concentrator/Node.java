package com.firstlinecode.sand.client.things.concentrator;

public class Node<T> {
	private String deviceId;
	private T address;
	
	public Node(String deviceId, T address) {
		this.deviceId = deviceId;
		this.address = address;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public T getAddress() {
		return address;
	}
	
}
