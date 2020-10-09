package com.firstlinecode.sand.emulators.things.emulators;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.client.things.IDeviceListener;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmFactory;

public abstract class AbstractCommunicationNetworkThingEmulator<OA, PA, D> extends AbstractThingEmulator
		implements ICommunicationNetworkThingEmulator<OA, PA, D>, ICommunicationListener<OA, PA, D> {
	protected ICommunicator<OA, PA, D> communicator;
	protected IObmFactory obmFactory = ObmFactory.createInstance();
	protected boolean isDataReceiving;
	protected Map<Protocol, Class<?>> supportedActions;
	
	@SuppressWarnings("unchecked")
	public AbstractCommunicationNetworkThingEmulator(String model, ICommunicator<?, ?, ?> communicator) {
		super(model);
		
		this.communicator = (ICommunicator<OA, PA, D>)communicator;	
		isDataReceiving = false;
		
		supportedActions = createSupportedActions();
	}
	
	@Override
	public void startToReceiveData() {
		if (isDataReceiving)
			return;
		
		communicator.addCommunicationListener(this);
		doStartToReceiveData();
		isDataReceiving = true;
	}
	
	@Override
	public void stopDataReceving() {
		if (!isDataReceiving)
			return;
		
		doStopDataReceiving();
		communicator.removeCommunicationListener(this);
		
		isDataReceiving = false;
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
	public ICommunicator<OA, PA, D> getCommunicator() {
		return communicator;
	}
	
	@Override
	public void sent(PA to, D data) {}
	
	@Override
	public void received(PA from, D data) {
		processReceived(from, data);
	}
	
	protected void processReceived(PA from, D data) {
		Protocol protocol = readProtocol(data);
		if (protocol == null) {
			// TODO Unknown protocol.
			throw new RuntimeException(String.format("Unknown protocol. Data: %s.", getDataInfoString(data)));
		}
		
		Class<?> actionType = supportedActions.get(protocol);
		if (actionType == null) {
			// TODO Action not supported.
			throw new RuntimeException(String.format("Action not supported. Protocol is %s.", protocol));
		}
		
		try {
			processAction(readAction(actionType, data));
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void occurred(CommunicationException e) {}
	
	protected abstract Map<Protocol, Class<?>> createSupportedActions();
	protected abstract Protocol readProtocol(D data);
	protected abstract <A> A readAction(Class<A> actionType, D data);
	protected abstract String getDataInfoString(D data);
	protected abstract void processAction(Object action) throws ExecutionException;
}
