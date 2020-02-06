package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.Message;

public class LoraMessage extends Message<LoraAddress, byte[]> {

	public LoraMessage(LoraAddress address, byte[] data) {
		super(address, data);
	}

}
