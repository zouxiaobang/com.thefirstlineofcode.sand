package com.firstlinecode.sand.emulators.lora;

public interface ILoraChip {
	public enum Type {
		HIGH_POWER,
		NORMAL
	}
	
	void addListener(ILoraMessageListener listener);
	boolean removeListener(ILoraMessageListener listener);
	Type getType();
	void changeAddress(LoraAddress address);
	LoraAddress getAddress();
	boolean isSlept();
	void sleep();
	void sleep(int millis);
	void wakeUp();
	void send(LoraAddress to, byte[] message);
	void received(LoraAddress from, byte[] message);
}
