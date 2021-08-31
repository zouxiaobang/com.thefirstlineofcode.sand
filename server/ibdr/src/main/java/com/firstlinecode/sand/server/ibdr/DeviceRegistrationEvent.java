package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEvent;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class DeviceRegistrationEvent implements IEvent {
	private DeviceIdentity identity;
	private Object customizedTaskResult;
	
	public DeviceRegistrationEvent(DeviceIdentity identity) {
		this(identity, null);
	}
	
	public DeviceRegistrationEvent(DeviceIdentity identity, Object customizedTaskResult) {
		this.identity = identity;
		this.customizedTaskResult = customizedTaskResult;
	}
	
	public DeviceIdentity getIdentity() {
		return identity;
	}
	
	public void setIdentity(DeviceIdentity identity) {
		this.identity = identity;
	}
	
	public Object getCustomizedTaskResult() {
		return customizedTaskResult;
	}
	
	public void setCustomizedTaskResult(Object customizedTaskResult) {
		this.customizedTaskResult = customizedTaskResult;
	}
	
	@Override
	public Object clone() {
		return new DeviceRegistrationEvent(identity, customizedTaskResult);
	}
}
