package com.thefirstlineofcode.sand.client.actuator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.oxm.IOxmFactory;
import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.IError;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.LangText;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Stanza;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.InternalServerError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAllowed;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.RemoteServerTimeout;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ServiceUnavailable;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.UndefinedCondition;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.UnexpectedRequest;
import com.thefirstlineofcode.basalt.protocol.core.stream.error.StreamError;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.stanza.IIqListener;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.client.core.actuator.IActuator;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutorFactory;
import com.thefirstlineofcode.sand.client.core.actuator.ILanExecutionErrorProcessor;
import com.thefirstlineofcode.sand.client.core.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.core.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.client.core.concentrator.Node;
import com.thefirstlineofcode.sand.client.core.obx.IObxFactory;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecution;
import com.thefirstlineofcode.sand.protocols.core.Address;
import com.thefirstlineofcode.sand.protocols.core.BadAddressException;
import com.thefirstlineofcode.sand.protocols.core.CommunicationNet;
import com.thefirstlineofcode.sand.protocols.core.ITraceId;
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;

public class Actuator implements IActuator, IIqListener {
	private static final Logger logger = LoggerFactory.getLogger(Actuator.class);
	
	private static final long DEFAULT_VALUE_OF_DEFAULT_LAN_EXECUTION_TIMEOUT = 1000 * 5;
	private static final int DEFAULT_LAN_EXECUTION_TIMEOUT_CHECK_INTERVAL = 500;
	
	private IChatServices chatServices;
	private Map<Class<?>, IExecutorFactory<?>> executorFactories;
	private IConcentrator concentrator;
	private ITraceIdFactory traceIdFactory;
	private Map<CommunicationNet, ICommunicator<?, ?, byte[]>> communicators;
	private Map<CommunicationNet, LanExecutionAnswerListener<?, ?>> netToLanExecutionAnswerListeners;
	private Map<String, List<LanExecutionTraceInfo>> lanNodeToLanExecutionTraceInfos;
	private Map<String, ILanExecutionErrorProcessor> modelToLanExecutionErrorProcessors;
	private long defaultLanExecutionTimeout;
	private int lanExecutionTimeoutCheckInterval;
	private ExpiredLanExecutionsChecker expiredLanExecutionsChecker;
	private IObxFactory obxFactory;
	private IOxmFactory oxmFactory;
	private JabberId host;
	private boolean started;
	private boolean lanEnabled;
	
	public Actuator(IChatServices chatServices) {
		this.chatServices = chatServices;
		executorFactories = new HashMap<>();
		communicators = new HashMap<>();
		netToLanExecutionAnswerListeners = new HashMap<>();
		oxmFactory = chatServices.getStream().getOxmFactory();
		lanNodeToLanExecutionTraceInfos = new HashMap<>();
		modelToLanExecutionErrorProcessors = new HashMap<>();
		defaultLanExecutionTimeout = DEFAULT_VALUE_OF_DEFAULT_LAN_EXECUTION_TIMEOUT;
		lanExecutionTimeoutCheckInterval = DEFAULT_LAN_EXECUTION_TIMEOUT_CHECK_INTERVAL;
		host = JabberId.parse(chatServices.getStream().getStreamConfig().getHost());
		started = false;
		lanEnabled = false;
	}
	
	@Override
	public void setToConcentrator(IConcentrator concentrator, ITraceIdFactory traceIdFactory, IObxFactory obxFactory) {
		this.concentrator = concentrator;
		this.traceIdFactory = traceIdFactory;
		this.obxFactory = obxFactory;
	}
	
	@Override
	public void received(Iq iq) {
		Execution execution = iq.getObject();
		
		if (logger.isInfoEnabled()) {
			logger.info("Received a execution message which's action object is {} from '{}'.",
					execution.getAction(), iq.getFrom() == null ? host : iq.getFrom());
		}
		
		try {
			execute(iq, execution);
		} catch (ProtocolException e) {
			if (logger.isErrorEnabled())
				logger.error(String.format("Failed to execute the action %s which was sent from '%s' on device '%s'.",
						execution.getAction(), iq.getFrom() == null ? host : iq.getFrom(), iq.getTo()), e);
			
			throw e;
		} catch (RuntimeException e) {
			if (logger.isErrorEnabled())
				logger.error(String.format("Failed to execute the action %s which was sent from '%s' on device '%s'.",
						execution.getAction(), iq.getFrom() == null ? host : iq.getFrom()), e);
			
			throw new ProtocolException(new InternalServerError(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> void execute(Iq iq, Execution execution) {	
		JabberId from = iq.getFrom();
		if (from == null) {
			from = host;
		}
		
		T action = (T)execution.getAction();
		if (toDeviceItself(iq.getTo())) {
			if (logger.isInfoEnabled()) {
				logger.info("Try to execute the action {} which was sent from '{}' on device '{}'.", action, from, iq.getTo());
			}
			
			if (!executorFactories.containsKey(action.getClass())) {
				if (logger.isWarnEnabled()) {
					logger.warn("Action which's type is {} not supported by device '{}'.",
							action.getClass().getName(), iq.getTo());
				}
				
				throw new ProtocolException(new ServiceUnavailable(String.format(
						"Action which's type is %s not supported by device '%s'.",
						action.getClass().getName(), iq.getTo())));
			}
			
			IExecutor<T> executor = createExecutor(action);
			executor.execute(iq, action);
			
			Iq result = new Iq(Iq.Type.RESULT, iq.getId());
			setFromToAddresses(iq.getFrom(), iq.getTo(), result);
			chatServices.getIqService().send(result);
		} else if (toLanNode(iq.getTo())) {
			if (concentrator == null)
				throw new ProtocolException(new NotAllowed(String.format("Try to deliver action to LAN node from device '%s'. But the device isn't a concentrator."),
						iq.getTo().getBareIdString()));
			
			if (!lanEnabled) {
				throw new ProtocolException(new UnexpectedRequest("Actuator is being in LAN disabled mode."));
			}
			
			if (logger.isInfoEnabled()) {
				logger.info("Try to execute the action {} which was sent from '{}' on LAN node '{}'.", action, from, iq.getTo());
			}
			
			executeOnLanNode(iq, action, execution.isLanTraceable(), execution.getLanTimeout());
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Can't find the device which's JID is '{}' to execute the action which was sent from '{}'.",
						iq.getTo(), from);
			}
			
			throw new ProtocolException(new BadRequest(
					String.format("Can't find the device which's JID is '%s' to execute the action which was sent from '%s'.",
							iq.getTo(), from)));
		}
	}

	private boolean toLanNode(JabberId to) {
		return to != null && to.getResource() != null &&
				!IConcentrator.LAN_ID_CONCENTRATOR.equals(to.getResource());
	}
	
	private boolean toDeviceItself(JabberId to) {
		return to == null ||
				(to != null && to.getResource() == null) ||
				(to != null && to.getResource() != null &&
					IConcentrator.LAN_ID_CONCENTRATOR.equals(to.getResource()));
	}

	@SuppressWarnings("unchecked")
	private <T> IExecutor<T> createExecutor(T action) throws ProtocolException {
		IExecutorFactory<T> executorFactory = (IExecutorFactory<T>)executorFactories.get(action.getClass());
		if (executorFactory != null)
			return executorFactory.create();
		
		return null;
	}
	
	private IExecutor<?> createExecutor(Class<? extends IExecutor<?>> executorType) {
		IExecutor<?> executor = null;
		Constructor<?> constructor = null;
		try {
			constructor = (Constructor<?>)executorType.getConstructor(IChatServices.class);
			
			try {
				executor = (IExecutor<?>)constructor.newInstance(chatServices);
			} catch (Exception e) {
				throw new ProtocolException(new InternalServerError("Failed to create executor instance.", e));
			}
		} catch (NoSuchMethodException | SecurityException e) {
			constructor = createEmptyConstructor(executorType, constructor);
			try {
				executor = (IExecutor<?>)constructor.newInstance();
			} catch (Exception exception) {
				throw new ProtocolException(new InternalServerError("Failed to create executor instance.", exception));
			}
		}
		
		for (Field field : executorType.getFields()) {
			if (IChatServices.class.isAssignableFrom(field.getType())) {
				boolean oldAccesssiable = field.isAccessible();
				try {
					field.setAccessible(true);
					field.set(executor, chatServices);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					throw new ProtocolException(new InternalServerError("Can't set chat services to executor.", e));
				} finally {
					field.setAccessible(oldAccesssiable);
				}
				
				break;
			}
		}
		
		return executor;
	}
	
	private Constructor<?> createEmptyConstructor(Class<?> executorType,
			Constructor<?> constructor) {
		try {
			constructor = (Constructor<?>)executorType.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ProtocolException(new InternalServerError(String.format(
					"Can't create executor for executor type: %s.", executorType.getName()), e));			
		}
		
		return constructor;
	}

	@Override
	public boolean unregisterExecutor(Class<?> actionType) {
		return executorFactories.remove(actionType) != null;
	}

	@Override
	public void start() {
		if (started)
			return;
		
		if (chatServices.getStream() == null || chatServices.getStream().getConnection() == null ||
				!chatServices.getStream().getConnection().isConnected()) {
			throw new IllegalStateException("You should Connect to server before start actuator.");
		}
		
		if (chatServices.getIqService().getListener(Execution.PROTOCOL) != this)
			chatServices.getIqService().addListener(Execution.PROTOCOL, this);
		
		if (concentrator != null)
			setLanEnabled(true);
		
		started = true;
	}

	private void enableLan() {
		if (expiredLanExecutionsChecker != null)
			expiredLanExecutionsChecker.stop();
		
		expiredLanExecutionsChecker = new ExpiredLanExecutionsChecker();
		new Thread(expiredLanExecutionsChecker).start();
	}
	
	@Override
	public void stop() {
		if (!started)
			return;
		
		if (concentrator != null)
			setLanEnabled(false);
		
		chatServices.getIqService().removeListener(Execution.PROTOCOL);
		
		started = false;
	}

	@SuppressWarnings("unchecked")
	private <OA, PA extends Address> void removeLanExecutionAnswerListener(CommunicationNet net,
			ICommunicator<OA, PA, byte[]> communicator) {
		LanExecutionAnswerListener<OA, PA> lanExecutionAnswerListener = (LanExecutionAnswerListener<OA, PA>)netToLanExecutionAnswerListeners.get(net);
		if (lanExecutionAnswerListener != null)
			communicator.removeCommunicationListener(lanExecutionAnswerListener);
	}
	
	private class ExpiredLanExecutionsChecker implements Runnable {
		private boolean stop;
		
		public ExpiredLanExecutionsChecker() {
			stop = false;
		}
		
		public void stop() {
			stop = true;
		}

		@Override
		public void run() {
			while (!stop) {
				checkExpiredLanExecutions();
				
				try {
					Thread.sleep(lanExecutionTimeoutCheckInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void checkExpiredLanExecutions() {
			long currentTime = Calendar.getInstance().getTime().getTime();
			synchronized (Actuator.this) {
				for (List<LanExecutionTraceInfo> traceInfos : lanNodeToLanExecutionTraceInfos.values()) {
					List<LanExecutionTraceInfo> expiredLanExecutions = new ArrayList<>();
					for (LanExecutionTraceInfo traceInfo : traceInfos) {
						if (Long.compare(currentTime, traceInfo.expiredTime) > 0) {
							expiredLanExecutions.add(traceInfo);
						}
					}
					
					if (expiredLanExecutions.size() > 0) {
						for (LanExecutionTraceInfo expiredLanExecution : expiredLanExecutions) {
							traceInfos.remove(expiredLanExecution);
							processExpiredLanExecution(expiredLanExecution.from, expiredLanExecution.to, expiredLanExecution.sanzaId);
						}
					}
				}
			}
		}
	}
	
	@Override
	public <T> void registerExecutor(Class<T> actionType, Class<? extends IExecutor<T>> executorType) {
		registerExecutorFactory(actionType, new CreateByTypeExecutorFactory<T>(executorType));
	}
	
	private class CreateByTypeExecutorFactory<T> implements IExecutorFactory<T> {
		private Class<? extends IExecutor<T>> executorType;
		
		public CreateByTypeExecutorFactory(Class<? extends IExecutor<T>> executorType) {
			this.executorType = executorType;
		}

		@SuppressWarnings("unchecked")
		@Override
		public IExecutor<T> create() {
			return (IExecutor<T>)createExecutor(executorType);
		}
		
	}
	
	@Override
	public <T> void registerExecutorFactory(Class<T> actionType, IExecutorFactory<T> executorFactory) {
		if (executorFactories.containsKey(actionType)) {
			throw new IllegalArgumentException(String.format("Reduplicate executor factory for action type: %s.", actionType));
		}
		
		ProtocolObject protocolObject = actionType.getAnnotation(ProtocolObject.class);
		if (protocolObject == null) {
			throw new IllegalArgumentException(String.format("Action type %s isn't a protocol object type.", actionType.getName()));
		}
		
		Protocol actionProtocol = new Protocol(protocolObject.namespace(), protocolObject.localName());
		oxmFactory.register(new IqProtocolChain(Execution.PROTOCOL).next(actionProtocol),
				new NamingConventionParserFactory<>(actionType));
		
		executorFactories.put(actionType, executorFactory);
	}
	
	@SuppressWarnings("unchecked")
	private <PA extends Address> void executeOnLanNode(Iq iq, Object action,
			boolean lanTraceable, Integer lanTimeout) {
		Node node = concentrator.getNode(iq.getTo().getResource());
		if (node == null)
			throw new ProtocolException(new ItemNotFound(String.format("LAN node '%s' not existed.", iq.getTo())));
		
		if (!concentrator.getModeRegistrar().isActionSupported(node.getModel(), action.getClass())) {
			throw new ProtocolException(new ServiceUnavailable(String.format(
					"Action type %s not supported by device '{}'.",
					action.getClass().getName(), iq.getTo())));
		}
		
		ICommunicator<?, PA, byte[]> communicator = (ICommunicator<?, PA, byte[]>)getCommunicator(node.getCommunicationNet());
		if (communicator == null) {
			throw new ProtocolException(new InternalServerError(String.format(
					"Communication net %s not supported by concentrator '{}'.",
					node.getCommunicationNet(), iq.getTo().getBareId())));
		}
		
		try {
			if (lanTraceable) {			
				LanExecution lanExecution = new LanExecution(traceIdFactory.generateRequestId(), action);
				traceLanExecution(iq.getFrom(), iq.getTo(), iq.getId(), node, lanExecution, lanTimeout);
				communicator.send((PA)node.getCommunicationNet().parse(node.getAddress()), obxFactory.toBinary(lanExecution));
			} else {
				communicator.send((PA)node.getCommunicationNet().parse(node.getAddress()), obxFactory.toBinary(action));			
			}
		} catch (BadAddressException e) {
			throw new ProtocolException(new InternalServerError(String.format("Bad communication network address: '%s'.", node.getAddress())));
		} catch (CommunicationException e) {
			throw new ProtocolException(new InternalServerError(String.format("Can't send request to node. Exception: %s.", e.getMessage())));
		}
	}

	private synchronized void traceLanExecution(JabberId from, JabberId to, String stanzaId,
			Node node, LanExecution lanExecution, Integer lanTimeout) {
		List<LanExecutionTraceInfo> lanExecutionTraceInfos = lanNodeToLanExecutionTraceInfos.get(node.getLanId());
		if (lanExecutionTraceInfos == null) {
			lanExecutionTraceInfos = lanNodeToLanExecutionTraceInfos.get(node.getDeviceId());
			if (lanExecutionTraceInfos == null) {
				lanExecutionTraceInfos = new ArrayList<>();
				lanNodeToLanExecutionTraceInfos.put(node.getLanId(), lanExecutionTraceInfos);
			}
		}
		
		long expiredTime;
		if (lanTimeout != null) {
			expiredTime = Calendar.getInstance().getTime().getTime() + (lanTimeout * 1000);
		} else {
			expiredTime = Calendar.getInstance().getTime().getTime() + defaultLanExecutionTimeout;
		}
		lanExecutionTraceInfos.add(new LanExecutionTraceInfo(from, to, stanzaId, node, lanExecution, expiredTime));
	}
	
	private class LanExecutionTraceInfo {
		public JabberId from;
		public JabberId to;
		public String sanzaId;
		public Node node;
		public LanExecution lanExecution;
		public long expiredTime;
		
		public LanExecutionTraceInfo(JabberId from, JabberId to, String sanzaId, Node node,
				LanExecution lanExecution, long expiredTime) {
			this.from = from;
			this.to = to;
			this.sanzaId = sanzaId;
			this.node = node;
			this.lanExecution = lanExecution;
			this.expiredTime = expiredTime;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <OA extends Address, PA extends Address> ICommunicator<OA, PA, byte[]> getCommunicator(CommunicationNet communicationNet) {
		ICommunicator<OA, PA, byte[]> communicator = (ICommunicator<OA, PA, byte[]>)communicators.get(communicationNet);
		if (communicator == null) {
			synchronized(this) {
				communicator = (ICommunicator<OA, PA, byte[]>)communicators.get(communicationNet);
				if (communicator != null)
					return communicator;
				
				communicator = (ICommunicator<OA, PA, byte[]>)concentrator.getCommunicator(communicationNet);
				if (communicator != null) {
					communicators.put(communicationNet, communicator);				
					LanExecutionAnswerListener<OA, PA> lanExecutionAnswerListener = new LanExecutionAnswerListener<OA, PA>(communicationNet);
					communicator.addCommunicationListener(lanExecutionAnswerListener);
					netToLanExecutionAnswerListeners.put(communicationNet, lanExecutionAnswerListener);
				}
			}
		}
		
		return communicator;
	}
	
	private class LanExecutionAnswerListener<OA, PA extends Address> implements ICommunicationListener<OA, PA, byte[]> {
		private CommunicationNet communicationNet;
		
		public LanExecutionAnswerListener(CommunicationNet communicationNet) {
			this.communicationNet = communicationNet;
		}
		
		@Override
		public void sent(PA to, byte[] data) {}

		@Override
		public void received(PA from, byte[] data) {
			Actuator.this.received(communicationNet, from, data);
		}
		
		@Override
		public void occurred(CommunicationException e) {}

		@Override
		public void addressChanged(OA newAddress, OA oldAddress) {}
	}
	
	private synchronized <PA extends Address> void received(CommunicationNet net, PA from, byte[] data) {
		if (!isLanExecutionMessage(data))
			return;
		
		LanExecution response = (LanExecution)obxFactory.toObject(data);
		ITraceId responseId = response.getTraceId();
		for (Node node : concentrator.getNodes().values()) {
			try {
				if (net.parse(node.getAddress()).equals(from)) {
					synchronized (lanNodeToLanExecutionTraceInfos) {
						List<LanExecutionTraceInfo> lanExecutionTraceInfos = lanNodeToLanExecutionTraceInfos.get(node.getLanId());
						if (lanExecutionTraceInfos != null) {
							LanExecutionTraceInfo request = null;
							for (LanExecutionTraceInfo lanExecutionTraceInfo : lanExecutionTraceInfos) {
								if (lanExecutionTraceInfo.lanExecution.getTraceId().isResponse(responseId.getBytes())) {
									request = lanExecutionTraceInfo;
									processLanExecutionResponse(lanExecutionTraceInfo.from, lanExecutionTraceInfo.to, lanExecutionTraceInfo.sanzaId);
									break;
								} else if (lanExecutionTraceInfo.lanExecution.getTraceId().isError(responseId.getBytes())) {
									processLanExecutionError(lanExecutionTraceInfo, response);
									request = lanExecutionTraceInfo;
									break;
								} else {
									if (logger.isErrorEnabled()) {
										logger.error("Can't find the request trace ID which generate the response ID: {}. Maybe the request is timeout.", BinaryUtils.getHexStringFromBytes(data));
									}
								}
							}
							
							lanExecutionTraceInfos.remove(request);
						}
					}
				}
			} catch (BadAddressException e) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Bad address: %s.", node.getAddress()), e);
				}
				
				throw new RuntimeException("Bad address: " + node.getAddress());
			}
		}
	}
	
	private void processLanExecutionError(LanExecutionTraceInfo traceInfo, LanExecution error) {
		if (!(error.getLanActionObj() instanceof String))
			throw new RuntimeException("An LAN action error code must be an string object.");
		
		String errorCode = (String)error.getLanActionObj();
		ILanExecutionErrorProcessor lanExecutionErrorProcessor = modelToLanExecutionErrorProcessors.get(traceInfo.node.getModel());
		if (lanExecutionErrorProcessor != null) {
			IError e = lanExecutionErrorProcessor.processErrorCode(errorCode);
			
			if (e instanceof StreamError) {
				chatServices.getStream().send(e);
				if (e.closeStream())
					chatServices.getStream().close();
			} else {
				StanzaError se = (StanzaError)e;
				
				se.setId(traceInfo.sanzaId);
				setFromToAddresses(traceInfo.from, traceInfo.to, se);
				
				chatServices.getStream().send(e);
			}
		} else {
			StanzaError e = new UndefinedCondition(StanzaError.Type.MODIFY);
			e.setId(traceInfo.sanzaId);
			setFromToAddresses(traceInfo.from, traceInfo.to, e);
			e.setText(new LangText(ThingsUtils.getExecutionErrorDescription(traceInfo.node.getModel(), errorCode)));
			
			chatServices.getStream().send((IError)e);
		}
		
	}

	private void processLanExecutionResponse(JabberId from, JabberId to, String stanzaId) {
		Iq result = new Iq(Iq.Type.RESULT, stanzaId);
		setFromToAddresses(from, to, result);
		
		chatServices.getIqService().send(result);
	}

	private void setFromToAddresses(JabberId from, JabberId to, Stanza stanza) {
		if (toLanNode(to))
			stanza.setFrom(to.getBareId());
		
		if (from != null && !host.equals(from)) {
			stanza.setTo(from);
		}
	}
	
	private void processExpiredLanExecution(JabberId from, JabberId to, String stanzaId) {
		Stanza timeout = new RemoteServerTimeout();
		timeout.setId(stanzaId);
		setFromToAddresses(from, to, timeout);
		
		chatServices.getStream().send(timeout);
	}
	
	private boolean isLanExecutionMessage(byte[] data) {
		return LanExecution.PROTOCOL.equals(obxFactory.readProtocol(data));
	}
	
	@Override
	public boolean unregisterLanAction(Class<?> lanActionType) {
		ProtocolObject protocolObject = lanActionType.getAnnotation(ProtocolObject.class);
		if (protocolObject == null) {
			throw new IllegalArgumentException(String.format("LAN action type %s isn't a protocol object type.", lanActionType.getName()));
		}
		
		Protocol lanActionProtocol = new Protocol(protocolObject.namespace(), protocolObject.localName());
		oxmFactory.unregister(new IqProtocolChain(Execution.PROTOCOL).next(lanActionProtocol));

		return obxFactory.unregisterLanAction(lanActionType);
	}

	@Override
	public void setTraceIdFactory(ITraceIdFactory traceIdFactory) {
		this.traceIdFactory = traceIdFactory;
	}

	@Override
	public ITraceIdFactory getTraceIdFactory() {
		return traceIdFactory;
	}

	@Override
	public void setDefaultLanExecutionTimeout(long timeout) {
		this.defaultLanExecutionTimeout = timeout;
	}

	@Override
	public long getDefaultLanExecutionTimeout() {
		return defaultLanExecutionTimeout;
	}

	@Override
	public void setLanExecutionTimeoutCheckInterval(int interval) {
		this.lanExecutionTimeoutCheckInterval = interval;
	}

	@Override
	public int getLanExecutionTimeoutCheckInterval() {
		return lanExecutionTimeoutCheckInterval;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public boolean isStopped() {
		return !started;
	}
	
	@Override
	public void setLanEnabled(boolean enabled) {
		if (concentrator == null)
			throw new IllegalStateException("Can't change LAN enabled state because the actuator doesn't run on a concentrator.");
		
		if (enabled) {
			enableLan();
		} else {			
			disableLan();
		}
		
		this.lanEnabled = enabled;
	}

	private void disableLan() {
		if (expiredLanExecutionsChecker != null) {
			expiredLanExecutionsChecker.stop();
			expiredLanExecutionsChecker = null;
		}
		
		synchronized (this) {
			for (CommunicationNet net : communicators.keySet()) {
				ICommunicator<?, ?, byte[]> communicator = communicators.get(net);
				removeLanExecutionAnswerListener(net, communicator);
			}
			
			communicators.clear();
		}
	}

	@Override
	public boolean isLanEnabled() {
		return lanEnabled;
	}

	@Override
	public void registerLanAction(Class<?> lanActionType) {
		ProtocolObject protocolObject = lanActionType.getAnnotation(ProtocolObject.class);
		if (protocolObject == null) {
			throw new IllegalArgumentException(String.format("LAN action type %s isn't a protocol object type.", lanActionType.getName()));
		}
		
		Protocol lanActionProtocol = new Protocol(protocolObject.namespace(), protocolObject.localName());
		oxmFactory.register(new IqProtocolChain(Execution.PROTOCOL).next(lanActionProtocol),
				new NamingConventionParserFactory<>(lanActionType));
		
		obxFactory.registerLanAction(lanActionType);
	}
	
	@Override
	public void registerLanExecutionErrorProcessor(ILanExecutionErrorProcessor lanExecutionErrorProcessor) {
		modelToLanExecutionErrorProcessors.put(lanExecutionErrorProcessor.getModel(), lanExecutionErrorProcessor);
	}
}
