package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.AbstractDualLoraChipCommunicator;
import com.firstlinecode.sand.client.lora.DualLoraAddress;
import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;

public class DualLoraChipCommunicator extends AbstractDualLoraChipCommunicator {
	protected ICommunicationListener<LoraAddress, byte[]> masterChipListener;
	protected ICommunicationListener<LoraAddress, byte[]> slaveChipListener;
	
	private DualLoraChipCommunicator(ILoraNetwork network, LoraAddress masterChipAddress,
			LoraAddress slaveChipAddress, LoraChipCreationParams params) {
		this(network.createChip(masterChipAddress, params), network.createChip(slaveChipAddress, params));
	}
	
	private DualLoraChipCommunicator(ILoraChip masterChip, ILoraChip slaveChip) {
		super(masterChip, slaveChip);
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
		return (ILoraChip)network.createChip(address, new LoraChipCreationParams(LoraChip.Type.HIGH_POWER));
	}
	
	@Override
	public boolean changeAddress(DualLoraAddress address) {
		try {			
			((LoraChip)masterChip).changeAddress(new LoraAddress(address.getAddress(),
					address.getMasterChipFrequencyBand()));
			((LoraChip)slaveChip).changeAddress(new LoraAddress(address.getAddress(),
					address.getSlaveChipFrequencyBand()));
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
}
