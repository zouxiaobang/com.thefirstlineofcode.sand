package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public class DualLoraChipCommunicator implements ICommunicator<LoraAddress, byte[]> {
	public enum CommunicationMode {
		ADDRESS_CONFIGURATION,
		WORKING
	}
	
	private ILoraNetwork network;
	protected LoraAddress address;
	protected ILoraChip masterChip;
	protected ILoraChip salveChip;
	
	public DualLoraChipCommunicator(ILoraNetwork network, LoraAddress address,
			int masterChipFrequencyBand, int slaveChipFrequencyBand) {
		this.network = network;
		this.address = address;
		
		masterChip = createLoraChip(masterChipFrequencyBand);
		salveChip = createLoraChip(slaveChipFrequencyBand);
	}
	
	protected ILoraChip createLoraChip(int masterChipFrequencyBand) {
		network.createChip(address, null);
		// TODO Auto-generated method stub
		return null;
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
