package com.firstlinecode.sand.client.lora;

import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.concentrator.IActionDeliverer;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class ActionDeliverer implements IActionDeliverer<LoraAddress, ObmData> {
	private ICommunicator<?, LoraAddress, ObmData> communicator;
	private IObmFactory obmFactory = ObmFactory.createInstance();

	@Override
	public void setCommunicator(ICommunicator<?, LoraAddress, ObmData> communicator) {
		this.communicator = communicator;
	}

	@Override
	public void deliver(LoraAddress to, Object action) throws CommunicationException {
		// TODO Auto-generated method stub
		communicator.send(to, new ObmData(action, obmFactory.toBinary(action)));
	}

}
