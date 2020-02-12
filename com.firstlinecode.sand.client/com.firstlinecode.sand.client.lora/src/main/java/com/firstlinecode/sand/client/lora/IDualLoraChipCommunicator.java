package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.CommunicationException;

public interface IDualLoraChipCommunicator {
	ILoraChip getMasterChip();
	ILoraChip getSlaveChip();
	void send(LoraAddress to, byte[] data) throws CommunicationException;
	LoraData receive();
	DualLoraAddress getAddress();
	void changeAddress(DualLoraAddress address) throws CommunicationException;
	void setAddressChangedListener(AddressChangedListener listener);
	void removeAddressChangedListener(AddressChangedListener listener);
	
	public interface AddressChangedListener {
		void addressChanged(DualLoraAddress newAddress, DualLoraAddress oldAddress);
	}
}
