package com.firstlinecode.sand.client.lora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.firstlinecode.chalk.core.IOrder;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public abstract class AbstractCommunicator<OA, PA, D> implements ICommunicator<OA, PA, D> {
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
