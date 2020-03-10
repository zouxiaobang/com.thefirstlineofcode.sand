package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public interface IDualLoraChipCommunicator {
	ILoraChip getMasterChip();
	ILoraChip getSlaveChip();
	void send(LoraAddress to, byte[] data) throws CommunicationException;
	LoraData receive();
	DualLoraAddress getAddress();
	void changeAddress(DualLoraAddress address) throws CommunicationException;
}
