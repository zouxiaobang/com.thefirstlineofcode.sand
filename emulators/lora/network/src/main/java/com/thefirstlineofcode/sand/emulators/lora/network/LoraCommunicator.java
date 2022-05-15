package com.thefirstlineofcode.sand.emulators.lora.network;

import com.thefirstlineofcode.sand.client.core.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.lora.AbstractCommunicator;
import com.thefirstlineofcode.sand.client.lora.ILoraChip;
import com.thefirstlineofcode.sand.client.lora.LoraData;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class LoraCommunicator extends AbstractCommunicator<LoraAddress, LoraAddress, byte[]> {
	protected ILoraChip chip;
	
	LoraCommunicator(ILoraChip chip) {
		this.chip = chip;
	}
	
	@Override
	public LoraAddress getAddress() {
		return chip.getAddress();
	}
	
	@Override
	protected void doChangeAddress(LoraAddress address) throws CommunicationException {
		chip.changeAddress(address);
	}
	
	@Override
	protected void doSend(LoraAddress to, byte[] data) throws CommunicationException {
		chip.send(to, data);
	}
	
	public LoraData receive() {
		LoraData data = (LoraData) chip.receive();
		if (data != null) {
			received(data.getAddress(), data.getData());
		}
		return data;
	}
	
	public ILoraChip getChip() {
		return chip;
	}
}
