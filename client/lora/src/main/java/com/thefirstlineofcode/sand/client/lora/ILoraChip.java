package com.thefirstlineofcode.sand.client.lora;

import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationChip;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public interface ILoraChip extends ICommunicationChip<LoraAddress, byte[]> {
	public enum PowerType {
		HIGH_POWER,
		NORMAL
	}
	
	boolean isSlept();
	void sleep();
	void sleep(int millis);
	void wakeUp();
	
	PowerType getPowerType();
	LoraAddress getAddress();
}
