package com.firstlinecode.sand.client.actuator;

import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.chalk.IChatServices;
import com.firstlinecode.chalk.core.stanza.IIqListener;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class Actuator implements IActuator, IIqListener {
	private Map<Class<?>, Class<? extends IExecutor<?>>> executors;
	private IExecutor<Object> defaultExecutor;
	
	public Actuator(IChatServices chatServices) {
		executors = new HashMap<>();		
		chatServices.getIqService().addListener(Execute.PROTOCOL, this);
	}

	@Override
	public <T> void registerExecutor(Class<T> actionType, Class<IExecutor<T>> executorType) {
		if (executors.containsKey(actionType))
			throw new IllegalArgumentException(String.format("Reduplicate executors for action type '%s'.", actionType.getName()));
		
		executors.put(actionType, executorType);
	}

	@Override
	public void setDefaultExecutor(IExecutor<Object> defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}

	@Override
	public void received(Iq iq) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean unregisterExecutor(Class<?> actionType) {
		return executors.remove(actionType) != null;
	}

}
