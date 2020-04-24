package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.granite.framework.core.event.IEvent;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class ExecutionEvent implements IEvent {
	private String deviceLocation;
	private Execute execute;
	
	public ExecutionEvent(String deviceLocation, Execute execute) {
		this.deviceLocation = deviceLocation;
		this.execute = execute;
	}
	
	public String getDeviceLocation() {
		return deviceLocation;
	}
	
	public Execute getExecute() {
		return execute;
	}
}
