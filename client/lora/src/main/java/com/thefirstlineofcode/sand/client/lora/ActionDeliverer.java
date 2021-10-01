package com.thefirstlineofcode.sand.client.lora;

import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.things.concentrator.IActionDeliverer;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.client.things.obm.ObmFactory;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class ActionDeliverer implements IActionDeliverer<LoraAddress, byte[]> {
	private ICommunicator<?, LoraAddress, byte[]> communicator;
	private IObmFactory obmFactory = ObmFactory.createInstance();

	@Override
	public void setCommunicator(ICommunicator<?, LoraAddress, byte[]> communicator) {
		this.communicator = communicator;
	}

	@Override
	public void deliver(LoraAddress to, Object action) throws CommunicationException {
		// TODO Auto-generated method stub
		communicator.send(to, obmFactory.toBinary(action));
	}

}
