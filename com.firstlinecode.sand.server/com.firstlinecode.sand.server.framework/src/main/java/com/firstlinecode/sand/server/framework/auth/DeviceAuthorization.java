package com.firstlinecode.sand.server.framework.auth;

import java.util.Date;

public class DeviceAuthorization {
	private String deviceId;
	private String authorizer;
	private Date authorizedTime;
	private Date expiredTime;
	private boolean canceled;
	
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
	
	public Date getAuthorizedTime() {
		return authorizedTime;
	}
	
	public void setAuthorizedTime(Date authorizeTime) {
		this.authorizedTime = authorizeTime;
	}
	
	public Date getExpiredTime() {
		return expiredTime;
	}
	
	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
}
