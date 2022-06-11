package com.thefirstlineofcode.sand.demo.app.android;

import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;

import java.util.Objects;

public class Device {
	private String deviceId;
	private String deviceLocation;
	private AccessControlEntry ace;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceLocation() {
		return deviceLocation;
	}
	
	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}
	
	public AccessControlEntry getAce() {
		return ace;
	}
	
	public void setAce(AccessControlEntry ace) {
		this.ace = ace;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		
		if (o == null || getClass() != o.getClass())
			return false;
		
		Device device = (Device) o;
		return Objects.equals(deviceId, device.deviceId) &&
				Objects.equals(deviceLocation, device.deviceLocation) &&
				Objects.equals(ace, device.ace);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(deviceId, deviceLocation, ace);
	}
}
