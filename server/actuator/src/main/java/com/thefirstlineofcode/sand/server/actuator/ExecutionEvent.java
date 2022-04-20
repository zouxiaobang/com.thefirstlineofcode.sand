package com.thefirstlineofcode.sand.server.actuator;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.server.devices.Device;

public class ExecutionEvent implements IEvent {
	private Device device;
	private String nodeLanId;
	private Execution execution;
	private IExecutionCallback callback;
	
	public ExecutionEvent(Device device, Execution execution) {
		this(device, null, execution);
	}
	
	public ExecutionEvent(Device device, String nodeLanId, Execution execute) {
		this(device, null, execute, null);
	}
	
	public ExecutionEvent(Device device, String nodeLanId, Execution execution, IExecutionCallback callback) {
		this.device = device;
		this.nodeLanId = nodeLanId;
		this.execution = execution;
		this.callback = callback;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public String getNodeLanId() {
		return nodeLanId;
	}
	
	public Execution getExecution() {
		return execution;
	}
	
	public IExecutionCallback getExecutionCallback() {
		return callback;
	}
	
	@Override
	public Object clone() {
		return new ExecutionEvent(device, nodeLanId, execution, callback);
	}
}
