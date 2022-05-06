package com.thefirstlineofcode.sand.emulators.commons;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.client.things.IDeviceListener;
import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactoryAware;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecution;
import com.thefirstlineofcode.sand.protocols.core.Address;

public abstract class AbstractCommunicationNetworkThingEmulator<OA, PA extends Address> extends AbstractThingEmulator
		implements ICommunicationNetworkThingEmulator<OA, PA, byte[]>, ICommunicationListener<OA, PA, byte[]>,
			IObmFactoryAware {
	protected ICommunicator<OA, PA, byte[]> communicator;
	protected IObmFactory obmFactory;
	protected boolean dataReceiving;
	protected Map<Protocol, Class<?>> supportedActions;
	
	public AbstractCommunicationNetworkThingEmulator() {}
	
	@SuppressWarnings("unchecked")
	public AbstractCommunicationNetworkThingEmulator(String type, String model, ICommunicator<?, ?, ?> communicator) {
		super(type, model);
		
		this.communicator = (ICommunicator<OA, PA, byte[]>)communicator;	
		dataReceiving = false;
		
		supportedActions = createSupportedActions();
	}
	
	@Override
	public void startToReceiveData() {
		if (dataReceiving)
			return;
		
		communicator.addCommunicationListener(this);
		doStartToReceiveData();
		dataReceiving = true;
	}
	
	@Override
	public void stopDataReceving() {
		if (!dataReceiving)
			return;
		
		doStopDataReceiving();
		communicator.removeCommunicationListener(this);
		
		dataReceiving = false;
	}
	
	protected abstract void doStartToReceiveData();
	protected abstract void doStopDataReceiving();
	
	@Override
	public void reset() {
		deviceId = generateDeviceId();
		
		doReset();
		
		getPanel().updateStatus(getThingStatus());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass()))
			return false;
		
		return deviceId.equals(((ICommunicationNetworkThingEmulator<?, ?, ?>)obj).getDeviceId());
	}
	
	@Override
	public void addDeviceListener(IDeviceListener listener) {
		deviceListeners.add(listener);
	}
	
	@Override
	public boolean removeDeviceListener(IDeviceListener listener) {
		return deviceListeners.remove(listener);
	}
	
	@Override
	public ICommunicator<OA, PA, byte[]> getCommunicator() {
		return communicator;
	}
	
	@Override
	public void sent(PA to, byte[] data) {}
	
	@Override
	public void received(PA from, byte[] data) {
		processReceived(from, data);
	}
	
	protected void processReceived(PA from, byte[] data) {
		Protocol protocol = readProtocol(data);
		if (protocol == null) {
			throw new RuntimeException(String.format("Unrecognized protocol. Data: %s.", getDataInfoString(data)));
		}
		
		if (LanExecution.PROTOCOL.equals(protocol)) {
			processLanExecution(from, data);
		} else {
			processAction(from, protocol, data);
		}
	}

	private void processAction(PA from, Protocol protocol, byte[] data)  {
		Class<?> actionType = supportedActions.get(protocol);
		Object action = readAction(actionType, data);
		
		if (actionType == null) {
			throw new RuntimeException(String.format("Action not supported. Protocol is %s.", protocol));
		}
		
		try {
			processAction(action);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void processLanExecution(PA from, byte[] data) {
		LanExecution request = (LanExecution)obmFactory.toObject(data);		
		Object action = request.getLanActionObj();
		
		try {
			processAction(action);
			sendResponseToPeer(from, request);
		} catch (ExecutionException e) {
			sendErrorToPeer(from, request, e.getErrorCode());
		}
	}

	protected void sendResponseToPeer(PA from, LanExecution request) {
		sendToPeer(from, new LanExecution(request.getTraceId().createResponseId()));
	}

	protected void sendToPeer(PA from, LanExecution response) {
		try {
			communicator.send(from, obmFactory.toBinary(response));
		} catch (CommunicationException ce) {
			ce.printStackTrace();
		}
	}

	protected void sendErrorToPeer(PA from, LanExecution request, String errorCode) {
		sendToPeer(from, new LanExecution(request.getTraceId().createErrorId(), errorCode));
	}
	
	@Override
	protected void doWriteExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(dataReceiving);
	}
	
	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		dataReceiving = in.readBoolean();
		
		supportedActions = createSupportedActions();
	}
	
	public boolean isDataReceiving() {
		return dataReceiving;
	}
	
	@Override
	public void setObmFactory(IObmFactory obmFactory) {
		this.obmFactory = obmFactory;
	}

	@Override
	public void occurred(CommunicationException e) {}
	
	protected Protocol readProtocol(byte[] data) {
		return obmFactory.getBinaryXmppProtocolConverter().readProtocol(data);
	}
	
	protected <A> A readAction(Class<A> actionType, byte[] data) {
		return (A)obmFactory.toObject(actionType, data);
	}
	
	protected String getDataInfoString(byte[] data) {
		return BinaryUtils.getHexStringFromBytes(data);
	}
	
	protected abstract Map<Protocol, Class<?>> createSupportedActions();
	protected abstract void processAction(Object action) throws ExecutionException;
}
