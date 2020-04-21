package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.granite.framework.core.event.IEvent;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class ExecutionEvent implements IEvent {
	private Execute execute;
	
	public ExecutionEvent(Execute execute) {
		this.execute = execute;
	}
	
	public Execute getExecute() {
		return execute;
	}
}
