package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public interface ILoraChip extends ICommunicationChip<LoraAddress, byte[]> {
	boolean isSlept();
	void sleep();
	void sleep(int millis);
	void wakeUp();
}
