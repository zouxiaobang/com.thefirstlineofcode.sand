package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public interface IDualLoraChipsCommunicator extends ICommunicator<DualLoraAddress, LoraAddress, byte[]> {
	ILoraChip getMasterChip();
	ILoraChip getSlaveChip();
	LoraData receive();
}
