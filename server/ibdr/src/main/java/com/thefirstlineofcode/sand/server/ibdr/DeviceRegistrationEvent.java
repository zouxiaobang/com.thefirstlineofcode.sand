package com.thefirstlineofcode.sand.server.ibdr;

import java.util.Date;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;

public class DeviceRegistrationEvent implements IEvent {
	private String deviceId;
	private String deviceName;
	private String authorizer;
	private Date registrationTime;
	
	public DeviceRegistrationEvent(String deviceId, String deviceName,
			String authorizer, Date registrationTime) {
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.authorizer = authorizer;
		this.registrationTime = registrationTime;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public String getAuthorizer() {
		return authorizer;
	}
	
	public Date getRegistrationTime() {
		return registrationTime;
	}
	
	@Override
	public Object clone() {
		return new DeviceRegistrationEvent(deviceId, deviceName, authorizer, registrationTime);
	}
}
