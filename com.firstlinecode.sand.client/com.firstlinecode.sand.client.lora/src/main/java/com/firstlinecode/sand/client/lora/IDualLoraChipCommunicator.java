package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;

public interface IDualLoraChipCommunicator {
	public ILoraChip getMasterChip();
	public ILoraChip getSlaveChip();
	public void send(LoraAddress to, byte[] data);
	public void received(LoraAddress from, byte[] data);
	public boolean changeAddress(DualLoraAddress address);
	public void addMasterChipListener(ICommunicationListener<LoraAddress, byte[]> listener);
	public void removeMasterListener(ICommunicationListener<LoraAddress, byte[]> listener);
	public void addSlaveChipListener(ICommunicationListener<LoraAddress, byte[]> listener);
	public void removeSlaveListener(ICommunicationListener<LoraAddress, byte[]> listener);
}
