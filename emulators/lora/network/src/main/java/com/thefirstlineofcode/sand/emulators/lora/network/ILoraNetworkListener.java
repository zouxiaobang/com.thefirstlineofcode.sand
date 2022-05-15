package com.thefirstlineofcode.sand.emulators.lora.network;

import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationNetworkListener;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public interface ILoraNetworkListener extends ICommunicationNetworkListener<LoraAddress, byte[]> {
	void collided(LoraAddress from, LoraAddress to, byte[] data);
	void lost(LoraAddress from, LoraAddress to, byte[] data);
}
