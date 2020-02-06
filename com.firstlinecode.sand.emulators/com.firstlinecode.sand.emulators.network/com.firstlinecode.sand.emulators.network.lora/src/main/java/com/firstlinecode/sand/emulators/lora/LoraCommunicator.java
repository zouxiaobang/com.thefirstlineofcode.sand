package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public class LoraCommunicator implements ICommunicator<LoraAddress, byte[]> {
	protected ILoraNetwork network;
	protected LoraAddress address;
	
	public LoraCommunicator(ILoraNetwork network, LoraAddress address) {
		this.network = network;
		this.address = address;
	}

	@Override
	public LoraAddress getAddress() {
		return address;
	}

	@Override
	public void send(LoraAddress to, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCommunicationListener(ICommunicationListener<LoraAddress> listener) {
		// TODO Auto-generated method stub
		
	}

}
