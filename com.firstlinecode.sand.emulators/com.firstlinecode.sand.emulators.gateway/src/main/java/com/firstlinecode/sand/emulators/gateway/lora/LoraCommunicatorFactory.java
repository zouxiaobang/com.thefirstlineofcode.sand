package com.firstlinecode.sand.emulators.gateway.lora;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.commuication.ICommunicatorFactory;
import com.firstlinecode.sand.client.things.commuication.ParamsMap;
import com.firstlinecode.sand.emulators.lora.ILoraNetwork;
import com.firstlinecode.sand.emulators.lora.LoraChipCreationParams;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;

public class LoraCommunicatorFactory implements ICommunicatorFactory{
	protected ILoraNetwork network;
	
	public LoraCommunicatorFactory(ILoraNetwork network) {
		this.network = network;
	}

	@Override
	public ICommunicator<?, ?> createCommunicator(ParamsMap params) {
		return new LoraCommunicator(network.createChip(getChipAddress(params)));
	}

	private LoraAddress getChipAddress(ParamsMap params) {
		return (LoraAddress)params.getParam(LoraChipCreationParams.PARAM_NAME_ADDRESS);
	}
}
