package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;

public interface ILoraChip extends ICommunicationChip<LoraAddress> {
	boolean isSlept();
	void sleep();
	void sleep(int millis);
	void wakeUp();
}
