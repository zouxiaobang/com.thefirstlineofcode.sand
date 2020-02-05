package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;

public interface ILoraChip extends ICommunicationChip<LoraAddress> {
	public enum Type {
		HIGH_POWER,
		NORMAL
	}
	
	Type getType();
	boolean isSlept();
	void sleep();
	void sleep(int millis);
	void wakeUp();
}
