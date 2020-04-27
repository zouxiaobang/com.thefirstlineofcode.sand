package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.granite.framework.core.event.IEvent;
import com.firstlinecode.sand.protocols.actuator.Execute;
import com.firstlinecode.sand.server.device.Device;

public class ExecutionEvent implements IEvent {
	private Device device;
	private String nodeLanId;
	private Execute execute;
	
	public ExecutionEvent(Device device, Execute execute) {
		this(device, null, execute);
	}
	
	public ExecutionEvent(Device device, String nodeLanId, Execute execute) {
		this.device = device;
		this.nodeLanId = nodeLanId;
		this.execute = execute;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public String getNodeLanId() {
		return nodeLanId;
	}
	
	public Execute getExecute() {
		return execute;
	}
}
