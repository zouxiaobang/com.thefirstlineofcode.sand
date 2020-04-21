package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.granite.framework.core.event.IEventContext;
import com.firstlinecode.granite.framework.core.event.IEventListener;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class ExecutionListener implements IEventListener<ExecutionEvent> {

	@Override
	public void process(IEventContext context, ExecutionEvent event) {
		// TODO Auto-generated method stub
		Execute execute = event.getExecute();
		
		System.out.println("Execute: " + execute);
	}

}
