package com.thefirstlineofcode.sand.server.actuator;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.sand.server.devices.Device;

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
	
	@Override
	public Object clone() {
		return new ExecutionEvent(device, nodeLanId, execute);
	}
}
