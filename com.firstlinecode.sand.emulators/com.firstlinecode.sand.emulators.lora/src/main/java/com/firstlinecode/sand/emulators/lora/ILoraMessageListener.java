package com.firstlinecode.sand.emulators.lora;

public interface ILoraMessageListener {
	void received(LoraAddress from, byte[] message);
}
