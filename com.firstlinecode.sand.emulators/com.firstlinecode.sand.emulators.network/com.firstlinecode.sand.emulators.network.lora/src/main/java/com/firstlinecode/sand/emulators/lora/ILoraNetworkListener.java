package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetworkListener;

public interface ILoraNetworkListener extends ICommunicationNetworkListener<LoraAddress, byte[]> {
	void collided(LoraAddress from, LoraAddress to, byte[] data);
	void lost(LoraAddress from, LoraAddress to, byte[] data);
}
