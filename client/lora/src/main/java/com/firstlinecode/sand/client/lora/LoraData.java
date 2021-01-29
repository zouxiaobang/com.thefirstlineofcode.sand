package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.Data;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class LoraData extends Data<LoraAddress, byte[]> {
	public LoraData(LoraAddress address, byte[] data) {
		super(address, data);
	}
}
