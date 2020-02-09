package com.firstlinecode.sand.client.lora.configuration;

import com.firstlinecode.sand.client.lora.IDualLoraChipCommunicator;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;

public class DynamicAddressConfigurator implements IAddressConfigurator<IDualLoraChipCommunicator>,
			ICommunicationListener<LoraAddress, byte[]> {
	public enum DynamicConfigurationState {
		WAITING,
		NEGOTIATING,
		CONFIRMING
	}
	
	private IDualLoraChipCommunicator communicator;
	private String nodeDeviceId;
	private LoraAddress nodeAddress;
	
	public DynamicAddressConfigurator(IDualLoraChipCommunicator communicator) {
		this.communicator = communicator;
	}
	
	@Override
	public void introduce() {
		communicator.addSlaveChipListener(this);
	}

	@Override
	public void negotiate() {
		// TODO Auto-generated method stub
	}

	@Override
	public void comfirm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		// TODO Auto-generated method stub
	}

	@Override
	public void occurred(CommunicationException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicator(IDualLoraChipCommunicator communicator) {
		// TODO Auto-generated method stub
		
	}

}
