package com.firstlinecode.sand.emulators.lora;

public class LoraMessage {
	private LoraAddress from;
	private byte[] message;
	
	public LoraMessage(LoraAddress from, byte[] message) {
		this.from = from;
		this.message = message;
	}
	
	public LoraAddress getForm() {
		return from;
	}
	
	public byte[] getMessage() {
		return message;
	}
}
