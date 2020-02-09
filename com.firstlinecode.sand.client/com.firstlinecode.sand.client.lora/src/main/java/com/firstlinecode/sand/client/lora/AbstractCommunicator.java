package com.firstlinecode.sand.client.lora;

import java.util.ArrayList;
import java.util.List;

import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public abstract class AbstractCommunicator<A, D> implements ICommunicator<A, D> {
	protected List<ICommunicationListener<A, D>> listeners;
	
	public AbstractCommunicator() {
		listeners = new ArrayList<>();
	}
	
	@Override
	public void changeAddress(A address) {
		try {
			doChangeAddress(address);
		} catch (CommunicationException e) {
			for (ICommunicationListener<A, D> listener : listeners) {
				listener.occurred(e);
			}
		}
	}
	
	@Override
	public void send(A to, D data) {
		try {
			doSend(to, data);
		} catch (CommunicationException e) {
			for (ICommunicationListener<A, D> listener : listeners) {
				listener.sent(to, data);
			}
		}
	}
	
	@Override
	public void addCommunicationListener(ICommunicationListener<A, D> listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeCommunicationListener(ICommunicationListener<A, D> listener) {
		listeners.remove(listener);
	}
	
	protected abstract void doChangeAddress(A address) throws CommunicationException;
	protected abstract void doSend(A to, D data) throws CommunicationException;
}
