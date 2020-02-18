package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.AbstractCommunicator;
import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;

public class LoraCommunicator extends AbstractCommunicator<LoraAddress, byte[]> {
	protected ILoraChip chip;
	
	public LoraCommunicator(ILoraChip chip) {
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
		// TODO
		return null;
	}
}
