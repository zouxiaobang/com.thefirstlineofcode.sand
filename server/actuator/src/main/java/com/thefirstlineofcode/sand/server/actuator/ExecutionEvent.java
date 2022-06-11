package com.thefirstlineofcode.sand.server.actuator;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEvent;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;

public class ExecutionEvent implements IEvent {
	private String deviceId;
	private String lanId;
	private Execution execution;
	private IExecutionCallback callback;
	
	public ExecutionEvent(String deviceId, Execution execution) {
		this(deviceId, null, execution);
	}
	
	public ExecutionEvent(String deviceId, String lanId, Execution execute) {
		this(deviceId, null, execute, null);
	}
	
	public ExecutionEvent(String deviceId, String lanId, Execution execution, IExecutionCallback callback) {
		this.deviceId = deviceId;
		this.lanId = lanId;
		this.execution = execution;
		this.callback = callback;
	}
	
	public String getDeviceId() {
		return deviceId;
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
		return new ExecutionEvent(deviceId, lanId, execution, callback);
	}
}
