package com.thefirstlineofcode.sand.emulators.things.emulators;

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
import com.thefirstlineofcode.sand.protocols.actuator.LanActionError;
import com.thefirstlineofcode.sand.protocols.actuator.LanActionException;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecute;
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
		
		if (LanExecute.PROTOCOL.equals(protocol)) {
			processLanExecute(from, data);
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
		} catch (LanActionException e) {
			e.printStackTrace();
		}
	}

	private void processLanExecute(PA from, byte[] data) {
		LanExecute lanExecute = (LanExecute)obmFactory.toObject(data);		
		Object action = lanExecute.getLanActionObj();
		
		try {
			processAction(action);
		} catch (LanActionException e) {
			processLanActionError(from, lanExecute, e.getError());
			return;
		}
		
		sendResponseToPeer(from, lanExecute);
	}

	private void sendResponseToPeer(PA from, LanExecute request) {
		LanExecute response = new LanExecute(request.getTraceId().createResponseId());		
		try {
			communicator.send(from, obmFactory.toBinary(response));
		} catch (CommunicationException ce) {
			ce.printStackTrace();
		}
	}

	private void processLanActionError(PA from, LanExecute request, LanActionError lanActionError) {
		LanExecute error = new LanExecute(request.getTraceId().createErrorId(), lanActionError);
		try {
			communicator.send(from, obmFactory.toBinary(error));
		} catch (CommunicationException ce) {
			ce.printStackTrace();
		}
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
	protected abstract void processAction(Object action) throws LanActionException;
}
