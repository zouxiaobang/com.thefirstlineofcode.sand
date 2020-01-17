package com.firstlinecode.sand.server.core.auth;

import java.util.Date;

public class DeviceAuthorization {
	private String deviceId;
	private String authorizer;
	private Date authorizeTime;
	private Date expiredTime;
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getAuthorizer() {
		return authorizer;
	}
	
	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}
	
	public Date getAuthorizeTime() {
		return authorizeTime;
	}
	
	public void setAuthorizeTime(Date authorizeTime) {
		this.authorizeTime = authorizeTime;
	}
	
	public Date getExpiredTime() {
		return expiredTime;
	}
	
	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}
}
