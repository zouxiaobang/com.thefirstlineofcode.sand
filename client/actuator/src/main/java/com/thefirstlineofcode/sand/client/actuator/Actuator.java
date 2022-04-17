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
import com.thefirstlineofcode.sand.client.things.actuator.IActuator;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutorFactory;
import com.thefirstlineofcode.sand.client.things.actuator.ILanActionErrorProcessor;
import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.things.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.client.things.concentrator.Node;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecute;
import com.thefirstlineofcode.sand.protocols.core.Address;
import com.thefirstlineofcode.sand.protocols.core.BadAddressException;
import com.thefirstlineofcode.sand.protocols.core.CommunicationNet;
import com.thefirstlineofcode.sand.protocols.core.ITraceId;
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;

public class Actuator implements IActuator, IIqListener {
	private static final Logger logger = LoggerFactory.getLogger(Actuator.class);
	
	private static final long DEFAULT_VALUE_OF_DEFAULT_LAN_EXECUTE_TIMEOUT = 1000 * 5;
	private static final int DEFAULT_LAN_EXECUTE_TIMEOUT_CHECK_INTERVAL = 500;
	
	private IChatServices chatServices;
	private Map<Class<?>, IExecutorFactory<?>> executorFactories;
	private String deviceModel;
	private IConcentrator concentrator;
	private ITraceIdFactory traceIdFactory;
	private Map<CommunicationNet, ICommunicator<?, ?, byte[]>> communicators;
	private Map<CommunicationNet, LanExecuteAnswerListener<?, ?>> netToLanExecuteAnswerListeners;
	private Map<String, List<LanExecuteTraceInfo>> lanNodeToLanExecuteTraceInfos;
	private Map<String, ILanActionErrorProcessor> modelToLanActionErrorProcessors;
	private long defaultLanExecuteTimeout;
	private int lanExecuteTimeoutCheckInterval;
	private ExpiredLanExecutesChecker expiredLanExecutesChecker;
	private IObmFactory obmFactory;
	private IOxmFactory oxmFactory;
	private JabberId host;
	private boolean started;
	private boolean lanEnabled;
	
	public Actuator(IChatServices chatServices) {
		this.chatServices = chatServices;
		executorFactories = new HashMap<>();
		communicators = new HashMap<>();
		netToLanExecuteAnswerListeners = new HashMap<>();
		oxmFactory = chatServices.getStream().getOxmFactory();
		lanNodeToLanExecuteTraceInfos = new HashMap<>();
		modelToLanActionErrorProcessors = new HashMap<>();
		defaultLanExecuteTimeout = DEFAULT_VALUE_OF_DEFAULT_LAN_EXECUTE_TIMEOUT;
		lanExecuteTimeoutCheckInterval = DEFAULT_LAN_EXECUTE_TIMEOUT_CHECK_INTERVAL;
		host = JabberId.parse(chatServices.getStream().getStreamConfig().getHost());
		started = false;
		lanEnabled = false;
	}
	
	@Override
	public void setDeviceModel(String deivceModel) {
		this.deviceModel = deivceModel;
	}
	
	@Override
	public void setToConcentrator(IConcentrator concentrator, ITraceIdFactory traceIdFactory, IObmFactory obmFactory) {
		this.concentrator = concentrator;
		this.traceIdFactory = traceIdFactory;
		this.obmFactory = obmFactory;
	}
	
	@Override
	public void received(Iq iq) {
		Execute execute = iq.getObject();
		
		if (logger.isInfoEnabled()) {
			logger.info("Received a execute message which's action object is {} from '{}'.",
					execute.getAction(), iq.getFrom() == null ? host : iq.getFrom());
		}
		
		try {
			execute(iq, execute);
		} catch (ProtocolException e) {
			if (logger.isErrorEnabled())
				logger.error(String.format("Failed to execute the action {} which was sent from '{}' on device '{}'.",
						execute.getAction(), iq.getFrom() == null ? host : iq.getFrom()), e);
			
			throw e;
		} catch (RuntimeException e) {
			if (logger.isErrorEnabled())
				logger.error(String.format("Failed to execute the action {} which was sent from '{}' on device '{}'.",
						execute.getAction(), iq.getFrom() == null ? host : iq.getFrom()), e);
			
			throw new ProtocolException(new InternalServerError(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> void execute(Iq iq, Execute execute) {	
		if (deviceModel == null)
			throw new IllegalStateException("Null device model. You should call setDeviceModel(String deviceModel) method before you start actuator.");
		
		JabberId from = iq.getFrom();
		if (from == null) {
			from = host;
		}
		
		T action = (T)execute.getAction();
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
			
			executeOnLanNode(iq, action, execute.isLanTraceable(), execute.getLanTimeout());
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Can't find the device which's JID is '{}' to execute the action which was sent from '%s'.",
						iq.getTo(), from));
			}
			
			throw new ProtocolException(new BadRequest(
					String.format("Can't find the device which's JID is '{}' to execute the action which was sent from '%s'.",
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
		
		if (chatServices.getIqService().getListener(Execute.PROTOCOL) != this)
			chatServices.getIqService().addListener(Execute.PROTOCOL, this);
		
		if (concentrator != null)
			setLanEnabled(true);
		
		started = true;
	}

	private void enableLan() {
		if (expiredLanExecutesChecker != null)
			expiredLanExecutesChecker.stop();
		
		expiredLanExecutesChecker = new ExpiredLanExecutesChecker();
		new Thread(expiredLanExecutesChecker).start();
	}
	
	@Override
	public void stop() {
		if (!started)
			return;
		
		if (concentrator != null)
			setLanEnabled(false);
		
		chatServices.getIqService().removeListener(Execute.PROTOCOL);
		
		started = false;
	}

	@SuppressWarnings("unchecked")
	private <OA, PA extends Address> void removeLanExecuteAnswerListener(CommunicationNet net,
			ICommunicator<OA, PA, byte[]> communicator) {
		LanExecuteAnswerListener<OA, PA> lanExecuteAnswerListener = (LanExecuteAnswerListener<OA, PA>)netToLanExecuteAnswerListeners.get(net);
		if (lanExecuteAnswerListener != null)
			communicator.removeCommunicationListener(lanExecuteAnswerListener);
	}
	
	private class ExpiredLanExecutesChecker implements Runnable {
		private boolean stop;
		
		public ExpiredLanExecutesChecker() {
			stop = false;
		}
		
		public void stop() {
			stop = true;
		}

		@Override
		public void run() {
			while (!stop) {
				checkExpiredLanExecutes();
				
				try {
					Thread.sleep(lanExecuteTimeoutCheckInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void checkExpiredLanExecutes() {
			long currentTime = Calendar.getInstance().getTime().getTime();
			synchronized (Actuator.this) {
				for (List<LanExecuteTraceInfo> traceInfos : lanNodeToLanExecuteTraceInfos.values()) {
					List<LanExecuteTraceInfo> expiredLanExecutes = new ArrayList<>();
					for (LanExecuteTraceInfo traceInfo : traceInfos) {
						if (Long.compare(currentTime, traceInfo.expiredTime) > 0) {
							expiredLanExecutes.add(traceInfo);
						}
					}
					
					if (expiredLanExecutes.size() > 0) {
						for (LanExecuteTraceInfo expiredLanExecute : expiredLanExecutes) {
							traceInfos.remove(expiredLanExecute);
							processExpiredLanExecute(expiredLanExecute.from, expiredLanExecute.to, expiredLanExecute.sanzaId);
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
		oxmFactory.register(new IqProtocolChain(Execute.PROTOCOL).next(actionProtocol),
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
				LanExecute lanExecute = new LanExecute(traceIdFactory.generateRequestId(), action);
				traceLanExecute(iq.getFrom(), iq.getTo(), iq.getId(), node, lanExecute, lanTimeout);
				communicator.send((PA)node.getCommunicationNet().parse(node.getAddress()), obmFactory.toBinary(lanExecute));
			} else {
				communicator.send((PA)node.getCommunicationNet().parse(node.getAddress()), obmFactory.toBinary(action));			
			}
		} catch (BadAddressException e) {
			throw new ProtocolException(new InternalServerError(String.format("Bad communication network address: '%s'.", node.getAddress())));
		} catch (CommunicationException e) {
			throw new ProtocolException(new InternalServerError(String.format("Can't send request to node. Exception: %s.", e.getMessage())));
		}
	}

	private synchronized void traceLanExecute(JabberId from, JabberId to, String stanzaId,
			Node node, LanExecute lanExecute, Integer lanTimeout) {
		List<LanExecuteTraceInfo> lanExecuteTraceInfos = lanNodeToLanExecuteTraceInfos.get(node.getLanId());
		if (lanExecuteTraceInfos == null) {
			lanExecuteTraceInfos = lanNodeToLanExecuteTraceInfos.get(node.getDeviceId());
			if (lanExecuteTraceInfos == null) {
				lanExecuteTraceInfos = new ArrayList<>();
				lanNodeToLanExecuteTraceInfos.put(node.getLanId(), lanExecuteTraceInfos);
			}
		}
		
		long expiredTime;
		if (lanTimeout != null) {
			expiredTime = Calendar.getInstance().getTime().getTime() + (lanTimeout * 1000);
		} else {
			expiredTime = Calendar.getInstance().getTime().getTime() + defaultLanExecuteTimeout;
		}
		lanExecuteTraceInfos.add(new LanExecuteTraceInfo(from, to, stanzaId, node, lanExecute, expiredTime));
	}
	
	private class LanExecuteTraceInfo {
		public JabberId from;
		public JabberId to;
		public String sanzaId;
		public Node node;
		public LanExecute lanExecute;
		public long expiredTime;
		
		public LanExecuteTraceInfo(JabberId from, JabberId to, String sanzaId, Node node,
				LanExecute lanExecute, long expiredTime) {
			this.from = from;
			this.to = to;
			this.sanzaId = sanzaId;
			this.node = node;
			this.lanExecute = lanExecute;
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
					LanExecuteAnswerListener<OA, PA> lanExecuteAnswerListener = new LanExecuteAnswerListener<OA, PA>(communicationNet);
					communicator.addCommunicationListener(lanExecuteAnswerListener);
					netToLanExecuteAnswerListeners.put(communicationNet, lanExecuteAnswerListener);
				}
			}
		}
		
		return communicator;
	}
	
	private class LanExecuteAnswerListener<OA, PA extends Address> implements ICommunicationListener<OA, PA, byte[]> {
		private CommunicationNet communicationNet;
		
		public LanExecuteAnswerListener(CommunicationNet communicationNet) {
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
		if (!isLanExecuteMessage(data))
			return;
		
		LanExecute response = (LanExecute)obmFactory.toObject(data);
		ITraceId responseId = response.getTraceId();
		for (Node node : concentrator.getNodes().values()) {
			try {
				if (net.parse(node.getAddress()).equals(from)) {
					synchronized (lanNodeToLanExecuteTraceInfos) {
						List<LanExecuteTraceInfo> lanExecuteTraceInfos = lanNodeToLanExecuteTraceInfos.get(node.getLanId());
						if (lanExecuteTraceInfos != null) {
							LanExecuteTraceInfo request = null;
							for (LanExecuteTraceInfo lanExecuteTraceInfo : lanExecuteTraceInfos) {
								if (lanExecuteTraceInfo.lanExecute.getTraceId().isResponse(responseId.getBytes())) {
									request = lanExecuteTraceInfo;
									processLanExecuteResponse(lanExecuteTraceInfo.from, lanExecuteTraceInfo.to, lanExecuteTraceInfo.sanzaId);
									break;
								} else if (lanExecuteTraceInfo.lanExecute.getTraceId().isError(responseId.getBytes())) {
									processLanExecuteError(lanExecuteTraceInfo, response);
									request = lanExecuteTraceInfo;
									break;
								} else {
									if (logger.isErrorEnabled()) {
										logger.error(String.format("Can't find the request trace ID which generate the response ID: %s. Maybe the request is timeout.", BinaryUtils.getHexStringFromBytes(data)));
									}
								}
							}
							
							lanExecuteTraceInfos.remove(request);
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
	
	private void processLanExecuteError(LanExecuteTraceInfo traceInfo, LanExecute error) {
		if (!(error.getLanActionObj() instanceof String))
			throw new RuntimeException("An LAN action error code must be an string object.");
		
		String errorCode = (String)error.getLanActionObj();
		ILanActionErrorProcessor lanActionErrorProcessor = modelToLanActionErrorProcessors.get(traceInfo.node.getModel());
		if (lanActionErrorProcessor != null) {
			IError e = lanActionErrorProcessor.processErrorCode(errorCode);
			
			if (e instanceof StreamError) {
				chatServices.getStream().send(e);
				if (e.closeStream())
					chatServices.getStream().close();
			} else {
				StanzaError se = (StanzaError)e;
				
				se.setId(traceInfo.sanzaId);
				setFromToAddresses(traceInfo.from, traceInfo.to, se);
				e.setText(getGlobalErrorCode(traceInfo.node.getModel(), errorCode));
				
				chatServices.getStream().send(e);
			}
		} else {
			StanzaError e = new UndefinedCondition(StanzaError.Type.MODIFY);
			e.setId(traceInfo.sanzaId);
			setFromToAddresses(traceInfo.from, traceInfo.to, e);
			e.setText(getGlobalErrorCode(traceInfo.node.getModel(), errorCode));
			
			chatServices.getStream().send((IError)e);
		}
		
	}
	
	private LangText getGlobalErrorCode(String model, String errorCode) {
		return new LangText(String.format("%s-E%s", model, errorCode));
	}

	private void processLanExecuteResponse(JabberId from, JabberId to, String stanzaId) {
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
	
	private void processExpiredLanExecute(JabberId from, JabberId to, String stanzaId) {
		Stanza timeout = new RemoteServerTimeout();
		timeout.setId(stanzaId);
		setFromToAddresses(from, to, timeout);
		
		chatServices.getStream().send(timeout);
	}
	
	private boolean isLanExecuteMessage(byte[] data) {
		return LanExecute.PROTOCOL.equals(obmFactory.readProtocol(data));
	}
	
	@Override
	public boolean unregisterLanAction(Class<?> lanActionType) {
		ProtocolObject protocolObject = lanActionType.getAnnotation(ProtocolObject.class);
		if (protocolObject == null) {
			throw new IllegalArgumentException(String.format("LAN action type %s isn't a protocol object type.", lanActionType.getName()));
		}
		
		Protocol lanActionProtocol = new Protocol(protocolObject.namespace(), protocolObject.localName());
		oxmFactory.unregister(new IqProtocolChain(Execute.PROTOCOL).next(lanActionProtocol));

		return obmFactory.unregisterLanAction(lanActionType);
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
	public void setDefaultLanExecuteTimeout(long timeout) {
		this.defaultLanExecuteTimeout = timeout;
	}

	@Override
	public long getDefaultLanExecuteTimeout() {
		return defaultLanExecuteTimeout;
	}

	@Override
	public void setLanExecuteTimeoutCheckInterval(int interval) {
		this.lanExecuteTimeoutCheckInterval = interval;
	}

	@Override
	public int getLanExecuteTimeoutCheckInterval() {
		return lanExecuteTimeoutCheckInterval;
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
		if (expiredLanExecutesChecker != null) {
			expiredLanExecutesChecker.stop();
			expiredLanExecutesChecker = null;
		}
		
		synchronized (this) {
			for (CommunicationNet net : communicators.keySet()) {
				ICommunicator<?, ?, byte[]> communicator = communicators.get(net);
				removeLanExecuteAnswerListener(net, communicator);
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
		// TODO Auto-generated method stub
		ProtocolObject protocolObject = lanActionType.getAnnotation(ProtocolObject.class);
		if (protocolObject == null) {
			throw new IllegalArgumentException(String.format("LAN action type %s isn't a protocol object type.", lanActionType.getName()));
		}
		
		Protocol lanActionProtocol = new Protocol(protocolObject.namespace(), protocolObject.localName());
		oxmFactory.register(new IqProtocolChain(Execute.PROTOCOL).next(lanActionProtocol),
				new NamingConventionParserFactory<>(lanActionType));
		
		obmFactory.registerLanAction(lanActionType);
	}
	
	@Override
	public void registerLanActionErrorProcessor(ILanActionErrorProcessor lanActionErrorProcessor) {
		modelToLanActionErrorProcessors.put(lanActionErrorProcessor.getModel(), lanActionErrorProcessor);
	}
}
