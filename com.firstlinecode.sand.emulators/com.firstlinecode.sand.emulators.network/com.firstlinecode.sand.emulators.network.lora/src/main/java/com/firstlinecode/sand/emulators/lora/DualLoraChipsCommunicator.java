package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.chalk.IOrder;
import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

import java.util.*;

public class DualLoraChipsCommunicator implements IDualLoraChipsCommunicator {
	private ILoraChip masterChip;
	private ILoraChip slaveChip;
	private List<ICommunicationListener<DualLoraAddress, LoraAddress, ObmData>> listeners;

	private DualLoraChipsCommunicator(ILoraNetwork network, LoraAddress masterChipAddress,
			LoraAddress slaveChipAddress, LoraChipCreationParams params) {
		this(network.createChip(masterChipAddress, params), network.createChip(slaveChipAddress, params));
	}
	
	private DualLoraChipsCommunicator(ILoraChip masterChip, ILoraChip slaveChip) {
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
		return (ILoraChip)network.createChip(address, new LoraChipCreationParams(LoraChip.Type.HIGH_POWER, null));
	}
	
	@Override
	public void send(LoraAddress to, ObmData data) throws CommunicationException {
		try {
			masterChip.send(to, data.getBinary());
		} catch (CommunicationException e) {
			for (ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> listener : listeners) {
				listener.occurred(e);
			}
			throw e;
		}
		for (ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> listener : listeners) {
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
			received(data.getAddress(), new ObmData(data.getData()));
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
			for (ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> listener : listeners) {
				listener.occurred(e);
			}
			throw e;
		}

		for (ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> listener : listeners) {
			listener.addressChanged(address, oldAddress);
		}
	}
	
	@Override
	public DualLoraAddress getAddress() {
		return new DualLoraAddress(masterChip.getAddress().getAddress(),
				masterChip.getAddress().getFrequencyBand() / 2);
	}

	@Override
	public void received(LoraAddress from, ObmData data) {
		for (ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> listener : listeners) {
			listener.received(from, data);
		}
	}

	@Override
	public void addCommunicationListener(ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> listener) {
		listeners.add(listener);
		Collections.sort(listeners, new OrderComparator<>());
	}

	@Override
	public void removeCommunicationListener(ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> listener) {
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
