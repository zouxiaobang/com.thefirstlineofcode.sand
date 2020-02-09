package com.firstlinecode.sand.client.lora;

import java.util.ArrayList;
import java.util.List;

import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;

public class AbstractDualLoraChipCommunicator implements IDualLoraChipCommunicator {
	protected ILoraChip masterChip;
	protected ILoraChip slaveChip;
	
	protected List<ICommunicationListener<LoraAddress, byte[]>> masterChipListeners;
	protected List<ICommunicationListener<LoraAddress, byte[]>> slaveChipListeners;
	
	public AbstractDualLoraChipCommunicator(ILoraChip masterChip, ILoraChip slaveChip) {
		this.masterChip = masterChip;
		this.slaveChip = slaveChip;
		
		masterChipListeners = new ArrayList<>();
		slaveChipListeners = new ArrayList<>();
	}
	
	@Override
	public void send(LoraAddress to, byte[] data) {
		try {
			masterChip.send(to, data);
			
			for (ICommunicationListener<LoraAddress, byte[]> listener : masterChipListeners) {
				listener.sent(to, data);
			}
		} catch (CommunicationException e) {
			for (ICommunicationListener<LoraAddress, byte[]> listener : masterChipListeners) {
				listener.occurred(e);
			}
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
	public void received(LoraAddress from, byte[] data) {
		for (ICommunicationListener<LoraAddress, byte[]> listener : slaveChipListeners) {
			listener.received(from, data);
		}
	}

	@Override
	public boolean changeAddress(DualLoraAddress address) {
		try {
			masterChip.changeAddress(address.getMasterAddress());
			slaveChip.changeAddress(address.getSlaveAddress());
			
			return true;
		} catch (CommunicationException e) {
			for (ICommunicationListener<LoraAddress, byte[]> listener : masterChipListeners) {
				listener.occurred(e);
			}
			
			return false;
		}
	}

	@Override
	public void addMasterChipListener(ICommunicationListener<LoraAddress, byte[]> listener) {
		masterChipListeners.add(listener);
	}

	@Override
	public void removeMasterListener(ICommunicationListener<LoraAddress, byte[]> listener) {
		masterChipListeners.remove(listener);
	}

	@Override
	public void addSlaveChipListener(ICommunicationListener<LoraAddress, byte[]> listener) {
		slaveChipListeners.add(listener);
	}

	@Override
	public void removeSlaveListener(ICommunicationListener<LoraAddress, byte[]> listener) {
		slaveChipListeners.remove(listener);
	}
	
}
