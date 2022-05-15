package com.thefirstlineofcode.sand.client.lora;

import com.thefirstlineofcode.sand.client.core.commuication.Data;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class LoraData extends Data<LoraAddress, byte[]> {
	public LoraData(LoraAddress address, byte[] data) {
		super(address, data);
	}
}
