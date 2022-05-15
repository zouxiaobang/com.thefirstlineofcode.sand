package com.thefirstlineofcode.sand.emulators.lora.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.thefirstlineofcode.chalk.core.IOrder;
import com.thefirstlineofcode.sand.client.core.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.lora.IDualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.client.lora.ILoraChip;
import com.thefirstlineofcode.sand.client.lora.LoraData;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class DualLoraChipsCommunicator implements IDualLoraChipsCommunicator {
	private ILoraChip masterChip;
	private ILoraChip slaveChip;
	private List<ICommunicationListener<DualLoraAddress, LoraAddress, byte[]>> listeners;
	
	protected DualLoraChipsCommunicator() {}
	
	protected DualLoraChipsCommunicator(ILoraNetwork network, LoraAddress masterChipAddress,
			LoraAddress slaveChipAddress, LoraChipCreationParams params) {
		this(network.createChip(masterChipAddress, params), network.createChip(slaveChipAddress, params));
	}
	
	protected DualLoraChipsCommunicator(ILoraChip masterChip, ILoraChip slaveChip) {
		this.listeners = new ArrayList<>();
		this.masterChip = masterChip;
		this.slaveChip = slaveChip;
	}
	
	public static DualLoraChipsCommunicator createInstance(ILoraChip masterLoraChip, ILoraChip slaveLoraChip) {
		DualLoraChipsCommunicator instance = new DualLoraChipsCommunicator(masterLoraChip, slaveLoraChip);
		
		return instance;
	}
	
	public static DualLoraChipsCommunicator createInstance(ILoraNetwork network, DualLoraAddress address, LoraChipCreationParams params) {
		DualLoraChipsCommunicator instance = new DualLoraChipsCommunicator(network.createChip(address.getMasterChipAddress(), params),
				network.createChip(address.getSlaveChipAddress(), params));
		
		return instance;
	}
	
	public static DualLoraChipsCommunicator createInstance(ILoraNetwork network, ILoraChip masterChip, ILoraChip slaveChip) {
		DualLoraChipsCommunicator instance = new DualLoraChipsCommunicator(masterChip, slaveChip);
		
		return instance;
	}
	
	protected static ILoraChip createLoraChip(ILoraNetwork network, LoraAddress address) {
		return (ILoraChip)network.createChip(address, new LoraChipCreationParams(LoraChip.PowerType.HIGH_POWER, null));
	}
	
	@Override
	public void send(LoraAddress to, byte[] data) throws CommunicationException {
		try {
			masterChip.send(to, data);
		} catch (CommunicationException e) {
			for (ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener : listeners) {
				listener.occurred(e);
			}
			throw e;
		}
		for (ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener : listeners) {
			listener.sent(to, data);
		}
	}
	
	public LoraAddress getMasterAddress() {
		return masterChip.getAddress();
	}
	
	public LoraAddress getSlaveAddress() {
		return slaveChip.getAddress();
	}
	
	public ILoraChip getMasterChip() {
		return masterChip;
	}
	
	public ILoraChip getSlaveChip() {
		return slaveChip;
	}
	
	@Override
	public LoraData receive() {
		LoraData data = (LoraData) slaveChip.receive();
		if (data != null) {
			received(data.getAddress(), data.getData());
		}

		return data;
	}

	@Override
	public void changeAddress(DualLoraAddress address) throws CommunicationException {
		DualLoraAddress oldAddress = this.getAddress();
		try {
			masterChip.changeAddress(address.getMasterChipAddress());
			slaveChip.changeAddress(address.getSlaveChipAddress());
		} catch (CommunicationException e) {
			for (ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener : listeners) {
				listener.occurred(e);
			}
			throw e;
		}

		for (ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener : listeners) {
			listener.addressChanged(address, oldAddress);
		}
	}
	
	@Override
	public DualLoraAddress getAddress() {
		return new DualLoraAddress(masterChip.getAddress().getAddress(),
				masterChip.getAddress().getFrequencyBand() / 2);
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		for (ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener : listeners) {
			listener.received(from, data);
		}
	}

	@Override
	public void addCommunicationListener(ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener) {
		listeners.add(listener);
		if (listeners.size() > 1) {			
			Collections.sort(listeners, new OrderComparator<ICommunicationListener<DualLoraAddress, LoraAddress, byte[]>>());
		}
	}

	@Override
	public void removeCommunicationListener(ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener) {
		listeners.remove(listener);
	}

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
