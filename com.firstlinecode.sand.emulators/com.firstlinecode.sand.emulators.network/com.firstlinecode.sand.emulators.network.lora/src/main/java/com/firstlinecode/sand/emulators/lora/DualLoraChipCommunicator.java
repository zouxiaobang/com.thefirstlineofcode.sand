package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.IDualLoraChipCommunicator;
import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class DualLoraChipCommunicator implements IDualLoraChipCommunicator {
	private ILoraChip masterChip;
	private ILoraChip slaveChip;
	
	private DualLoraChipCommunicator(ILoraNetwork network, LoraAddress masterChipAddress,
			LoraAddress slaveChipAddress, LoraChipCreationParams params) {
		this(network.createChip(masterChipAddress, params), network.createChip(slaveChipAddress, params));
	}
	
	private DualLoraChipCommunicator(ILoraChip masterChip, ILoraChip slaveChip) {
		this.masterChip = masterChip;
		this.slaveChip = slaveChip;
	}
	
	public static DualLoraChipCommunicator createInstance(ILoraChip masterLoraChip, ILoraChip slaveLoraChip) {
		DualLoraChipCommunicator instance = new DualLoraChipCommunicator(masterLoraChip, slaveLoraChip);
		
		return instance;
	}
	
	public static DualLoraChipCommunicator createInstance(ILoraNetwork network, DualLoraAddress address, LoraChipCreationParams params) {
		DualLoraChipCommunicator instance = new DualLoraChipCommunicator(network.createChip(address.getMasterAddress(), params),
				network.createChip(address.getSlaveAddress(), params));
		
		return instance;
	}
	
	public static DualLoraChipCommunicator createInstance(ILoraNetwork network, ILoraChip masterChip, ILoraChip slaveChip) {
		DualLoraChipCommunicator instance = new DualLoraChipCommunicator(masterChip, slaveChip);
		
		return instance;
	}
	
	protected static ILoraChip createLoraChip(ILoraNetwork network, LoraAddress address) {
		return (ILoraChip)network.createChip(address, new LoraChipCreationParams(LoraChip.Type.HIGH_POWER, null));
	}
	
	@Override
	public void send(LoraAddress to, byte[] data) throws CommunicationException {
		masterChip.send(to, data);
	}
	
	public LoraAddress getMasterAddress() {
		return masterChip.getAddress();
	}
	
	public LoraAddress getSlaveAddress() {
		return slaveChip.getAddress();
	}
	
	public ILoraChip getMasterChip() {
		return masterChip;
	}
	
	public ILoraChip getSlaveChip() {
		return slaveChip;
	}
	
	@Override
	public LoraData receive() {
		return (LoraData)slaveChip.receive();
	}

	@Override
	public void changeAddress(DualLoraAddress address) throws CommunicationException {
		masterChip.changeAddress(address.getMasterAddress());
		slaveChip.changeAddress(address.getSlaveAddress());
	}
	
	@Override
	public DualLoraAddress getAddress() {
		return new DualLoraAddress(masterChip.getAddress().getAddress(),
				masterChip.getAddress().getFrequencyBand() / 2);
	}
	
}
