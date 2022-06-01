package com.thefirstlineofcode.sand.server.actuator;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.server.devices.Device;

public class ExecutionEvent implements IEvent {
	private Device device;
	private String lanId;
	private Execution execution;
	private IExecutionCallback callback;
	
	public ExecutionEvent(Device device, Execution execution) {
		this(device, null, execution);
	}
	
	public ExecutionEvent(Device device, String lanId, Execution execute) {
		this(device, null, execute, null);
	}
	
	public ExecutionEvent(Device device, String lanId, Execution execution, IExecutionCallback callback) {
		this.device = device;
		this.lanId = lanId;
		this.execution = execution;
		this.callback = callback;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public String getLanId() {
		return lanId;
	}
	
	public Execution getExecution() {
		return execution;
	}
	
	public IExecutionCallback getExecutionCallback() {
		return callback;
	}
	
	@Override
	public Object clone() {
		return new ExecutionEvent(device, lanId, execution, callback);
	}
}
