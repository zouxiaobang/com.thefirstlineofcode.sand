package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.things.ICommunicationChip;

public interface ILoraChip extends ICommunicationChip<LoraAddress> {
	public enum Type {
		HIGH_POWER,
		NORMAL
	}
	
	void addListener(ILoraMessageListener listener);
	boolean removeListener(ILoraMessageListener listener);
	Type getType();
	boolean isSlept();
	void sleep();
	void sleep(int millis);
	void wakeUp();
}
