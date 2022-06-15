package com.thefirstlineofcode.sand.client.lora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.thefirstlineofcode.chalk.core.IOrder;
import com.thefirstlineofcode.sand.client.core.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicator;
import com.thefirstlineofcode.sand.protocols.core.Address;

public abstract class AbstractCommunicator<OA, PA extends Address, D> implements ICommunicator<OA, PA, D> {
	protected List<ICommunicationListener<OA, PA, D>> listeners;

	public AbstractCommunicator() {
		listeners = new ArrayList<>();
	}
	
	@Override
	public void changeAddress(OA address) throws CommunicationException {
		try {
			doChangeAddress(address);
		} catch (CommunicationException e) {
			for (ICommunicationListener<OA, PA, D> listener : listeners) {
				listener.occurred(e);
			}
			
			throw e;
		}
	}
	
	@Override
	public void send(PA to, D data) throws CommunicationException {
		try {
			doSend(to, data);
		} catch (CommunicationException e) {
			for (ICommunicationListener<OA, PA, D> listener : listeners) {
				listener.occurred(e);
			}
			
			throw e;
		}
		
		for (ICommunicationListener<OA, PA, D> listener : listeners) {
			listener.sent(to, data);
		}
	}
	
	@Override
	public void addCommunicationListener(ICommunicationListener<OA, PA, D> listener) {
		if (listeners.contains(listener))
			return;
		
		listeners.add(listener);
		Collections.sort(listeners, new OrderComparator<>());
	}
	
	@Override
	public void removeCommunicationListener(ICommunicationListener<OA, PA, D> listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void received(PA from, D data) {
		for (ICommunicationListener<OA, PA, D> listener : listeners) {
			listener.received(from, data);
		}
	}
	
	protected abstract void doChangeAddress(OA address) throws CommunicationException;
	protected abstract void doSend(PA to, D data) throws CommunicationException;

	private class OrderComparator<T> implements Comparator<T> {
		@Override
		public int compare(T obj1, T obj2) {
			int orderOfObj1 = IOrder.ORDER_NORMAL;
			int orderOfObj2 = IOrder.ORDER_NORMAL;

			if (obj1 instanceof IOrder) {
				IOrder order1 = (IOrder) obj1;
				orderOfObj1 = order1.getOrder();
			}

			if (obj2 instanceof IOrder){
				IOrder order2 = (IOrder) obj2;
				orderOfObj2 = order2.getOrder();
			}

			return orderOfObj2 - orderOfObj1;
		}
	}
}
