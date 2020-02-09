package com.firstlinecode.sand.emulators.gateway.lora;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.commuication.ICommunicatorFactory;
import com.firstlinecode.sand.emulators.lora.ILoraNetwork;

public class LoraCommunicatorFactory implements ICommunicatorFactory<LoraAddress, byte[]>{
	protected ILoraNetwork network;
	
	public LoraCommunicatorFactory(ILoraNetwork network) {
		this.network = network;
	}

	@Override
	public ICommunicator<LoraAddress, byte[]> createCommunicator() {
		// TODO Auto-generated method stub
		return null;
	}
}
