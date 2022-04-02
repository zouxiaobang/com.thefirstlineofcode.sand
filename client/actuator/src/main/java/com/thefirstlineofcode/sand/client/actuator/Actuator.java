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
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Stanza;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.InternalServerError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAllowed;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.RemoteServerTimeout;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.stanza.IIqListener;
import com.thefirstlineofcode.sand.client.things.actuator.IActuator;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutorFactory;
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
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;

public class Actuator implements IActuator, IIqListener {
	private static final Logger logger = LoggerFactory.getLogger(Actuator.class);
	
	private static final long DEFAULT_LAN_EXECUTE_TIMEOUT = 1000 * 5;
	private static final int DEFAULT_LAN_EXECUTE_TIMEOUT_CHECK_INTERVAL = 500;
	
	private IChatServices chatServices;
	private Map<Class<?>, IExecutorFactory<?>> executorFactories;
	private String deviceModel;
	private IConcentrator concentrator;
	private ITraceIdFactory traceIdFactory;
	private Map<CommunicationNet, ICommunicator<?, ?, byte[]>> communicators;
	private Map<String, List<LanExecuteTraceInfo>> lanNodeToLanExecuteTraceInfos;
	private long lanExecuteTimeout;
	private int lanExecuteTimeoutCheckInterval;
	private TimeoutLanExecutesChecker timeoutLanExecutesChecker;
	private IObmFactory obmFactory;
	private IOxmFactory oxmFactory;
	private JabberId host;
	private boolean started;
	
	public Actuator(IChatServices chatServices) {
		this.chatServices = chatServices;
		executorFactories = new HashMap<>();
		communicators = new HashMap<>();
		oxmFactory = chatServices.getStream().getOxmFactory();
		lanNodeToLanExecuteTraceInfos = new HashMap<>();
		lanExecuteTimeout = DEFAULT_LAN_EXECUTE_TIMEOUT;
		lanExecuteTimeoutCheckInterval = DEFAULT_LAN_EXECUTE_TIMEOUT_CHECK_INTERVAL;
		host = JabberId.parse(chatServices.getStream().getStreamConfig().getHost());
		started = false;
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
				
				throw new ProtocolException(new NotAllowed((String.format("Action which's type is %s not supported by device '%s'.",
						action.getClass().getName(), iq.getTo()))));
			}
			
			IExecutor<T> executor = createExecutor(action);
			executor.execute(iq, action);
		} else if (toLanNode(iq.getTo())) {
			if (logger.isInfoEnabled()) {
				logger.info("Try to execute the action {} which was sent from '{}' on LAN node '{}'.", action, from, iq.getTo());
			}
			
			if (concentrator == null)
				throw new ProtocolException(new NotAllowed(String.format("Try to deliver action to LAN node by device '%s'. But the device isn't a concentrator."),
						iq.getTo().getBareIdString()));
			
			executeOnLanNode(iq, action, execute.isLanTraceable());
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
		
		if (timeoutLanExecutesChecker != null)
			timeoutLanExecutesChecker.stop();
		
		timeoutLanExecutesChecker = new TimeoutLanExecutesChecker();
		new Thread(timeoutLanExecutesChecker).start();
		
		started = true;
	}
	
	@Override
	public void stop() {
		if (!started)
			return;
		
		chatServices.getIqService().removeListener(Execute.PROTOCOL);
		
		if (timeoutLanExecutesChecker != null) {
			timeoutLanExecutesChecker.stop();
			timeoutLanExecutesChecker = null;
		}
		
		started = false;
	}
	
	private class TimeoutLanExecutesChecker implements Runnable {
		private boolean stop;
		
		public TimeoutLanExecutesChecker() {
			stop = false;
		}
		
		public void stop() {
			stop = true;
		}

		@Override
		public void run() {
			if (stop)
				return;
			
			checkTimeoutLanExecutes();
			
			try {
				Thread.sleep(lanExecuteTimeoutCheckInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void checkTimeoutLanExecutes() {
			long currentTime = Calendar.getInstance().getTime().getTime();
			synchronized (Actuator.this) {
				for (List<LanExecuteTraceInfo> traceInfos : lanNodeToLanExecuteTraceInfos.values()) {
					List<LanExecuteTraceInfo> timeouts = new ArrayList<>();
					for (LanExecuteTraceInfo traceInfo : traceInfos) {
						if (Long.compare(currentTime, traceInfo.timeout) > 0) {
							timeouts.add(traceInfo);
						}
					}
					
					if (timeouts.size() > 0) {
						for (LanExecuteTraceInfo timeout : timeouts) {
							traceInfos.remove(timeout);
							processLanExecuteTimeout(timeout.from, timeout.to, timeout.sanzaId);
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
	private <PA extends Address> void executeOnLanNode(Iq iq, Object action, boolean lanTraceable) {
		Node node = concentrator.getNode(iq.getTo().getResource());
		if (node == null)
			throw new ProtocolException(new ItemNotFound(String.format("LAN node '%s' not existed.", iq.getTo())));
		
		if (!concentrator.getModeRegistrar().isActionSupported(node.getModel(), action.getClass())) {
			throw new ProtocolException(new NotAllowed((String.format("Action type %s not supported by device '{}'.",
					action.getClass().getName(), iq.getTo()))));
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
				traceLanExecute(iq.getFrom(), iq.getTo(), iq.getId(), node, lanExecute);
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

	private synchronized void traceLanExecute(JabberId from, JabberId to, String stanzaId, Node node, LanExecute lanExecute) {
		List<LanExecuteTraceInfo> lanExecuteTraceInfos = lanNodeToLanExecuteTraceInfos.get(node.getLanId());
		if (lanExecuteTraceInfos == null) {
			lanExecuteTraceInfos = lanNodeToLanExecuteTraceInfos.get(node.getDeviceId());
			if (lanExecuteTraceInfos == null) {
				lanExecuteTraceInfos = new ArrayList<>();
				lanNodeToLanExecuteTraceInfos.put(node.getLanId(), lanExecuteTraceInfos);
			}
		}
		
		long timeout = Calendar.getInstance().getTime().getTime() + lanExecuteTimeout;
		lanExecuteTraceInfos.add(new LanExecuteTraceInfo(from, to, stanzaId, lanExecute, timeout));
	}
	
	private class LanExecuteTraceInfo {
		public JabberId from;
		public JabberId to;
		public String sanzaId;
		public LanExecute lanExecute;
		public long timeout;
		
		public LanExecuteTraceInfo(JabberId from, JabberId to, String sanzaId, LanExecute lanExecute, long timeout) {
			this.from = from;
			this.to = to;
			this.sanzaId = sanzaId;
			this.lanExecute = lanExecute;
			this.timeout = timeout;
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
					communicator.addCommunicationListener(new LanExecuteAnswerListener<OA, PA>(communicationNet));
					communicators.put(communicationNet, communicator);				
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
		for (Node node : concentrator.getNodes().values()) {
			try {
				if (net.parse(node.getAddress()).equals(from)) {
					synchronized (lanNodeToLanExecuteTraceInfos) {
						List<LanExecuteTraceInfo> lanExecuteTraceInfos = lanNodeToLanExecuteTraceInfos.get(node.getLanId());
						if (lanExecuteTraceInfos != null) {
							LanExecuteTraceInfo request = null;
							for (LanExecuteTraceInfo lanExecuteTraceInfo : lanExecuteTraceInfos) {
								if (lanExecuteTraceInfo.lanExecute.getTraceId().isResponse(data)) {
									request = lanExecuteTraceInfo;
									processLanExecuteResponse(lanExecuteTraceInfo.from, lanExecuteTraceInfo.to, lanExecuteTraceInfo.sanzaId);
									break;
								} else if (lanExecuteTraceInfo.lanExecute.getTraceId().isError(data)) {
									processLanExecuteError(lanExecuteTraceInfo.from, lanExecuteTraceInfo.to,
											lanExecuteTraceInfo.sanzaId, lanExecuteTraceInfo.lanExecute, response);
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
	
	private void processLanExecuteError(JabberId from, JabberId to, String stanzaId, LanExecute request, LanExecute error) {
		if (!(error.getLanActionObj() instanceof StanzaError))
			throw new RuntimeException("An action error must be an stanza error object.");
		
		StanzaError stanzaError = (StanzaError)error.getLanActionObj();
		stanzaError.setId(stanzaId);
		setJidAddresses(from, to, stanzaError);
		
		chatServices.getStream().send(stanzaError);
	}
	
	private void processLanExecuteResponse(JabberId from, JabberId to, String stanzaId) {
		Iq result = new Iq(Iq.Type.RESULT, stanzaId);
		setJidAddresses(from, to, result);
		
		chatServices.getIqService().send(result);
	}

	private void setJidAddresses(JabberId from, JabberId to, Stanza stanza) {
		stanza.setFrom(to);
		if (!host.equals(from) && from != null) {
			stanza.setTo(from);
		}
	}
	
	private void processLanExecuteTimeout(JabberId from, JabberId to, String stanzaId) {
		StanzaError timeout = new RemoteServerTimeout();
		timeout.setId(stanzaId);
		setJidAddresses(from, to, timeout);
		
		chatServices.getStream().send(timeout);
	}
	
	private boolean isLanExecuteMessage(byte[] data) {
		return LanExecute.PROTOCOL.equals(obmFactory.readProtocol(data));
	}
	
	@Override
	public void registerLanAction(Class<?> lanActionType) {
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
	public void setLanExecuteTimeout(long timeout) {
		this.lanExecuteTimeout = timeout;
	}

	@Override
	public long getLanExecuteTimeout() {
		return lanExecuteTimeout;
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
	
}
