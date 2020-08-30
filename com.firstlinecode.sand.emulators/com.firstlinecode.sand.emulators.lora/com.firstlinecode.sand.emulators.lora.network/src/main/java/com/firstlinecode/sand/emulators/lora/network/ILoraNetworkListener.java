package com.firstlinecode.sand.emulators.lora.network;

import com.firstlinecode.sand.client.things.commuication.ICommunicationNetworkListener;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public interface ILoraNetworkListener extends ICommunicationNetworkListener<LoraAddress, byte[]> {
	void collided(LoraAddress from, LoraAddress to, byte[] data);
	void lost(LoraAddress from, LoraAddress to, byte[] data);
}
