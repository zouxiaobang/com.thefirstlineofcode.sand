package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class DualLoraChipsCommunicator implements IDualLoraChipsCommunicator {
	private ILoraChip masterChip;
	private ILoraChip slaveChip;
	
	private DualLoraChipsCommunicator(ILoraNetwork network, LoraAddress masterChipAddress,
			LoraAddress slaveChipAddress, LoraChipCreationParams params) {
		this(network.createChip(masterChipAddress, params), network.createChip(slaveChipAddress, params));
	}
	
	private DualLoraChipsCommunicator(ILoraChip masterChip, ILoraChip slaveChip) {
		this.masterChip = masterChip;
		this.slaveChip = slaveChip;
	}
	
	public static DualLoraChipsCommunicator createInstance(ILoraChip masterLoraChip, ILoraChip slaveLoraChip) {
		DualLoraChipsCommunicator instance = new DualLoraChipsCommunicator(masterLoraChip, slaveLoraChip);
		
		return instance;
	}
	
	public static DualLoraChipsCommunicator createInstance(ILoraNetwork network, DualLoraAddress address, LoraChipCreationParams params) {
		DualLoraChipsCommunicator instance = new DualLoraChipsCommunicator(network.createChip(address.getMasterChipAddress(), params),
				network.createChip(address.getSlaveChipAddress(), params));
		
		return instance;
	}
	
	public static DualLoraChipsCommunicator createInstance(ILoraNetwork network, ILoraChip masterChip, ILoraChip slaveChip) {
		DualLoraChipsCommunicator instance = new DualLoraChipsCommunicator(masterChip, slaveChip);
		
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
		masterChip.changeAddress(address.getMasterChipAddress());
		slaveChip.changeAddress(address.getSlaveChipAddress());
	}
	
	@Override
	public DualLoraAddress getAddress() {
		return new DualLoraAddress(masterChip.getAddress().getAddress(),
				masterChip.getAddress().getFrequencyBand() / 2);
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCommunicationListener(ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCommunicationListener(ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> listener) {
		// TODO Auto-generated method stub
		
	}
	
}
