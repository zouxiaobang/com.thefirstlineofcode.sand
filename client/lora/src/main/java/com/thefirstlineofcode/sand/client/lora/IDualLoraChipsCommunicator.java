package com.thefirstlineofcode.sand.client.lora;

import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public interface IDualLoraChipsCommunicator extends ICommunicator<DualLoraAddress, LoraAddress, byte[]> {
	ILoraChip getMasterChip();
	ILoraChip getSlaveChip();
	LoraData receive();
}
