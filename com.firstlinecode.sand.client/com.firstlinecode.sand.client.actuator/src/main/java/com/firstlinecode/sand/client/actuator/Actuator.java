package com.firstlinecode.sand.client.actuator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.chalk.IChatServices;
import com.firstlinecode.chalk.core.stanza.IIqListener;
import com.firstlinecode.sand.client.actuator.ExecutionException.Reason;
import com.firstlinecode.sand.client.concentrator.IActionDeliverer;
import com.firstlinecode.sand.client.concentrator.IConcentrator;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class Actuator implements IActuator, IIqListener {
	private static final String LAN_ID_CONCENTRATOR = "00";
	private IChatServices chatServices;
	
	private Map<Class<?>, IExecutorFactory<?>> executorFactories;
	
	public Actuator() {
		executorFactories = new HashMap<>();
	}
	
	@Override
	public void received(Iq iq) {
		Execute execute = iq.getObject();
		try {
			execute(iq, execute.getAction());
			
			Iq result = new Iq(Iq.Type.RESULT, iq.getId());
			chatServices.getIqService().send(result);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void execute(Iq iq, T action) throws ExecutionException {
		IExecutorFactory<T> executorFactory = (IExecutorFactory<T>)executorFactories.get(action.getClass());
		IExecutor<T> executor = executorFactory.create();
		
		if (executor != null) {
			executor.execute(iq, action);
		} else if (iq.getTo() != null && iq.getTo().getResource() != null &&
				!LAN_ID_CONCENTRATOR.equals(iq.getTo().getResource())) {
			deliverAction(iq, action);
		} else {
			throw new ExecutionException(Reason.UNSUPPORTED_ACTION_TYPE);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> IExecutor<T> createExecutor(Class<IExecutor<T>> executorType) throws ExecutionException {
		Constructor<T> constructor = null;
		try {
			constructor = (Constructor<T>)executorType.getConstructor(IChatServices.class);
		} catch (SecurityException | NoSuchMethodException e) {
			throw new ExecutionException(Reason.FAILED_TO_CREATE_INSTANCE);
		}
		
		if (constructor != null) {
			IExecutor<T> executor = null;
			try {
				executor = (IExecutor<T>)constructor.newInstance(chatServices);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new ExecutionException(Reason.FAILED_TO_CREATE_INSTANCE);
			}
			
			for (Field field : executorType.getFields()) {
				if (IChatServices.class.isAssignableFrom(field.getType())) {
					boolean oldAccesssiable = field.isAccessible();
					try {
						field.setAccessible(true);
						field.set(executor, chatServices);
					} catch (IllegalAccessException | IllegalArgumentException e) {
						throw new ExecutionException(Reason.FAILED_TO_CREATE_INSTANCE, e);
					} finally {
						field.setAccessible(oldAccesssiable);
					}
					
					break;
				}
			}
			
			return executor;
		}
		
		try {
			return executorType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ExecutionException(Reason.FAILED_TO_CREATE_INSTANCE);
		}
	}

	@Override
	public boolean unregisterExecutor(Class<?> actionType) {
		return executorFactories.remove(actionType) != null;
	}

	@Override
	public void start() {
		chatServices.getIqService().addListener(Execute.PROTOCOL, this);
	}

	@Override
	public void stop() {
		chatServices.getIqService().removeListener(this);
	}
	
	@Override
	public <T> void registerExecutor(Class<T> actionType, Class<IExecutor<T>> executorType) {
		if (executorFactories.containsKey(actionType))
			throw new IllegalArgumentException(String.format("Reduplicate executors for action type '%s'.", actionType.getName()));
		
		executorFactories.put(actionType, new CreateByTypeExecutorFactory<T>(executorType));
	}
	
	private class CreateByTypeExecutorFactory<T> implements IExecutorFactory<T> {
		private Class<IExecutor<T>> executorType;
		
		public CreateByTypeExecutorFactory(Class<IExecutor<T>> executorType) {
			this.executorType = executorType;
		}

		@Override
		public IExecutor<T> create() throws ExecutionException {
			return createExecutor(executorType);
		}
		
	}
	
	@Override
	public <T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory) {
		if (executorFactories.containsKey(actionType)) {
			throw new IllegalArgumentException(String.format("Reduplicate executor factory for action type '%s'.", actionType));
		}
		
		executorFactories.put(actionType, executorFactory);
	}
	
	private void deliverAction(Iq iq, Object action) throws ExecutionException {
		IConcentrator concentrator = chatServices.createApi(IConcentrator.class);
		if (concentrator == null)
			throw new ExecutionException(Reason.NOT_A_CONCENTRATOR);
		
		if (concentrator.getNode(iq.getTo().getResource()) == null)
			throw new ExecutionException(Reason.INVALID_NODE_LAN_ID);
		
		IActionDeliverer actionDeliverer = chatServices.createApi(IActionDeliverer.class);
		actionDeliverer.deliver(iq.getTo().getResource(), action);
	}

}
