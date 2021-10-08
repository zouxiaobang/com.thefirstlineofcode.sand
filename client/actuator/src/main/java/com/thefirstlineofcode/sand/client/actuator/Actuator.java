package com.thefirstlineofcode.sand.client.actuator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.parsing.IParserFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.stanza.IIqListener;
import com.thefirstlineofcode.sand.client.dmr.IModelRegistrar;
import com.thefirstlineofcode.sand.client.dmr.ModelRegistrar;
import com.thefirstlineofcode.sand.client.things.autuator.ExecutionException;
import com.thefirstlineofcode.sand.client.things.autuator.ExecutionException.Reason;
import com.thefirstlineofcode.sand.client.things.autuator.IActuator;
import com.thefirstlineofcode.sand.client.things.autuator.IExecutor;
import com.thefirstlineofcode.sand.client.things.autuator.IExecutorFactory;
import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.things.concentrator.IActionDeliverer;
import com.thefirstlineofcode.sand.client.things.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.client.things.concentrator.Node;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.sand.protocols.core.BadAddressException;
import com.thefirstlineofcode.sand.protocols.core.CommunicationNet;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public class Actuator implements IActuator, IIqListener {
	private static final Logger logger = LoggerFactory.getLogger(Actuator.class);
	
	private IChatServices chatServices;
	private Map<Class<?>, IExecutorFactory<?>> executorFactories;
	private ConcurrentMap<CommunicationNet, IActionDeliverer<?, ?>> actionDeliverers;
	
	private IModelRegistrar modelRegistrar;
	
	public Actuator() {
		executorFactories = new HashMap<>();
		actionDeliverers = new ConcurrentHashMap<>();
		
		modelRegistrar = new ModelRegistrar();
	}
	
	@Override
	public void received(Iq iq) {
		Execute execute = iq.getObject();
		
		if (logger.isInfoEnabled()) {
			logger.info("Received a execute message. Action object is {}.", execute.getAction());
		}
		
		try {
			execute(iq, execute.getAction());
			
			Iq result = new Iq(Iq.Type.RESULT, iq.getId());
			if (iq.getFrom() != null)
				iq.setTo(iq.getFrom());
			
			chatServices.getIqService().send(result);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private <T> void execute(Iq iq, T action) throws ExecutionException {
		try {
			IExecutor<T> executor = createExecutor(action);
			
			if (executor != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Try to execute the action {}.", action);
				}
				
				executor.execute(iq, action);
			} else if (iq.getTo() != null && iq.getTo().getResource() != null &&
					!IConcentrator.LAN_ID_CONCENTRATOR.equals(iq.getTo().getResource())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Try to deliver the action {} to node {}.", action, iq.getTo().getResource());
				}
				
				deliverAction(iq, action);
			} else {
				throw new ExecutionException(Reason.UNSUPPORTED_ACTION_TYPE,
						String.format("Unsupported action type: %s.", action.getClass().getName()));
			}
		} catch (RuntimeException e) {
			// TODO: handle exception
			throw new ExecutionException(Reason.UNKNOWN_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> IExecutor<T> createExecutor(T action) throws ExecutionException {
		IExecutorFactory<T> executorFactory = (IExecutorFactory<T>)executorFactories.get(action.getClass());
		if (executorFactory != null)
			return executorFactory.create();
		
		return null;
	}
	
	private IExecutor<?> createExecutor(Class<? extends IExecutor<?>> executorType) throws ExecutionException {
		IExecutor<?> executor = null;
		Constructor<?> constructor = null;
		try {
			constructor = (Constructor<?>)executorType.getConstructor(IChatServices.class);
			
			try {
				executor = (IExecutor<?>)constructor.newInstance(chatServices);
			} catch (Exception e) {
				throw new ExecutionException(Reason.FAILED_TO_CREATE_EXECUTOR_INSTANCE,
						getCantCreateExecutorInfo(executor.getClass()), e);
			}
		} catch (NoSuchMethodException e) {
			constructor = createEmptyConstructor(executorType, constructor);
			try {
				executor = (IExecutor<?>)constructor.newInstance();
			} catch (Exception nie) {
				throw new ExecutionException(Reason.FAILED_TO_CREATE_EXECUTOR_INSTANCE,
						getCantCreateExecutorInfo(executor.getClass()), nie);
			}
		} catch (SecurityException e) {
			throw new ExecutionException(Reason.FAILED_TO_CREATE_EXECUTOR_INSTANCE,
					getCantCreateExecutorInfo(executor.getClass()));			
		}
		
		for (Field field : executorType.getFields()) {
			if (IChatServices.class.isAssignableFrom(field.getType())) {
				boolean oldAccesssiable = field.isAccessible();
				try {
					field.setAccessible(true);
					field.set(executor, chatServices);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					throw new ExecutionException(Reason.FAILED_TO_CREATE_EXECUTOR_INSTANCE, "Can't set chat services to executor.", e);
				} finally {
					field.setAccessible(oldAccesssiable);
				}
					
				break;
			}
		}
			
		return executor;
	}

	private String getCantCreateExecutorInfo(Class<?> executorType) {
		return String.format("Can't create executor for executor type: %s.", executorType.getName());
	}

	private Constructor<?> createEmptyConstructor(Class<?> executorType,
			Constructor<?> constructor) throws ExecutionException {
		try {
			constructor = (Constructor<?>)executorType.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new ExecutionException(Reason.FAILED_TO_CREATE_EXECUTOR_INSTANCE,
					getCantCreateExecutorInfo(executorType), e);			
		} catch (SecurityException e) {
			throw new ExecutionException(Reason.FAILED_TO_CREATE_EXECUTOR_INSTANCE,
					getCantCreateExecutorInfo(executorType), e);
		}
		
		return constructor;
	}

	@Override
	public boolean unregisterExecutor(Class<?> actionType) {
		return executorFactories.remove(actionType) != null;
	}

	@Override
	public void start() {
		if (chatServices.getIqService().getListener(Execute.PROTOCOL) != this)
			chatServices.getIqService().addListener(Execute.PROTOCOL, this);		
	}
	
	@SuppressWarnings("unchecked")
	private <T> IParserFactory<T> createCustomActionParserFactory(Class<T> actionType) {
		String customActionParserFactoryName = String.format("%s.%s.%s", actionType.getPackage().getName(),
				actionType.getSimpleName(), "ParserFactory");
		try {
			Class<IParserFactory<T>> customActionTranslatorFactoryType = (Class<IParserFactory<T>>)Class.forName(customActionParserFactoryName);
			return customActionTranslatorFactoryType.newInstance();
		} catch (Exception e) {
			// Ignore
		}
		
		return null;
	}

	@Override
	public void stop() {
		chatServices.getIqService().removeListener(Execute.PROTOCOL);
	}
	
	@Override
	public <T> void registerExecutor(Class<T> actionType, Class<? extends IExecutor<T>> executorType) {
		if (executorFactories.containsKey(actionType))
			throw new IllegalArgumentException(String.format("Reduplicate executors for action type '%s'.", actionType.getName()));
		
		executorFactories.put(actionType, new CreateByTypeExecutorFactory<T>(executorType));
	}
	
	private class CreateByTypeExecutorFactory<T> implements IExecutorFactory<T> {
		private Class<? extends IExecutor<T>> executorType;
		
		public CreateByTypeExecutorFactory(Class<? extends IExecutor<T>> executorType) {
			this.executorType = executorType;
		}

		@SuppressWarnings("unchecked")
		@Override
		public IExecutor<T> create() throws ExecutionException {
			return (IExecutor<T>)createExecutor(executorType);
		}
		
	}
	
	@Override
	public <T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory) {
		if (executorFactories.containsKey(actionType)) {
			throw new IllegalArgumentException(String.format("Reduplicate executor factory for action type '%s'.", actionType));
		}
		
		executorFactories.put(actionType, executorFactory);
	}
	
	@SuppressWarnings("unchecked")
	private <PA> void deliverAction(Iq iq, Object action) throws ExecutionException {
		IConcentrator concentrator = chatServices.createApi(IConcentrator.class);
		if (concentrator == null)
			throw new ExecutionException(Reason.NOT_A_CONCENTRATOR);
		
		Node node = concentrator.getNode(iq.getTo().getResource());
		if (node == null)
			throw new ExecutionException(Reason.INVALID_NODE_LAN_ID);
		
		IActionDeliverer<PA, ?> actionDeliverer = getActionDeliverer(concentrator, node);
		if (actionDeliverer == null) {
			throw new ExecutionException(Reason.UNSUPPORTED_COMMUNICATION_NET);
		}
		
		try {
			actionDeliverer.deliver((PA)getNodeAddress(node.getCommunicationNet(), node.getAddress()), action);
		} catch (BadAddressException e) {
			throw new ExecutionException(Reason.BAD_ADDRESS, e);
		} catch (CommunicationException e) {
			throw new ExecutionException(Reason.FAILED_TO_DELIVER_ACTION_TO_NODE, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <PA> IActionDeliverer<PA, ?> getActionDeliverer(IConcentrator concentrator, Node node) throws ExecutionException {
		return (IActionDeliverer<PA, ?>)getActionDeliverer(
				node.getCommunicationNet(), concentrator.getCommunicator(node.getCommunicationNet()));
	}

	private Object getNodeAddress(CommunicationNet communicationNet, String address) throws BadAddressException {
		return communicationNet.parse(address);
	}

	private IActionDeliverer<?, ?> getActionDeliverer(CommunicationNet communicationNet,
			ICommunicator<?, ?, ?> communicator) throws ExecutionException {
		IActionDeliverer<?, ?> actionDeliverer = actionDeliverers.get(communicationNet);
		if (actionDeliverer == null) {
			actionDeliverer = createActionDeliverer(communicationNet, communicator);
			IActionDeliverer<?, ?> existed = actionDeliverers.putIfAbsent(communicationNet, actionDeliverer);
			if (existed != null) {
				actionDeliverer = existed;
			}
		}
		
		return actionDeliverer;
	}

	@SuppressWarnings("unchecked")
	private <PA, D> IActionDeliverer<PA, D> createActionDeliverer(CommunicationNet communicationNet,
			ICommunicator<?, PA, D> communicator) throws ExecutionException {
		String actionDelivererTypeName = String.format("com.thefirstlineofcode.sand.client.%s.ActionDeliverer",
				communicationNet.toString().toLowerCase());
		try {
			Class<?> actionDelivererType = Class.forName(actionDelivererTypeName);
			IActionDeliverer<PA, D> actionDeliverer = (IActionDeliverer<PA, D>)actionDelivererType.newInstance();
			actionDeliverer.setCommunicator(communicator);
			return actionDeliverer;
		} catch (Exception e) {
			throw new ExecutionException(Reason.FAILED_TO_CREATE_ACTION_DELIVERER_INSTANCE, e);
		}
	}

	@Override
	public void registerModel(ModelDescriptor modelDescriptor) {
		if (isModelRegistered(modelDescriptor)) {
			throw new IllegalArgumentException(String.format("Reduplicate model descritor. Model name is '%s'", modelDescriptor.getName()));
		}
		
		modelRegistrar.registerModeDescriptor(modelDescriptor);
		
		for (Protocol protocol : modelDescriptor.getSupportedActions().keySet()) {
			Class<?> actionType = modelDescriptor.getSupportedActions().get(protocol);
			IParserFactory<?> actionParserFactory = createCustomActionParserFactory(actionType);
			
			if (actionParserFactory == null)
				actionParserFactory = new NamingConventionParserFactory<>(actionType);
			
			chatServices.getStream().getOxmFactory().register(
					new IqProtocolChain(Execute.PROTOCOL).next(protocol),
					actionParserFactory);
		}
	}

	private boolean isModelRegistered(ModelDescriptor modelDescriptor) {
		for (ModelDescriptor aModelDescriptor : modelRegistrar.getModelDescriptors()) {
			if (aModelDescriptor.getName().equals(modelDescriptor.getName()))
				return true;
		}
		
		return false;
	}

}
