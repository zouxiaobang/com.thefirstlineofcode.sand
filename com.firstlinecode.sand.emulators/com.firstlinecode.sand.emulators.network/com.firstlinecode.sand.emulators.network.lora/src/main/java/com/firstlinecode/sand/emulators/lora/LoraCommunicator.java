package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.AbstractCommunicator;
import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class LoraCommunicator extends AbstractCommunicator<LoraAddress, LoraAddress, ObmData> {
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
	protected void doSend(LoraAddress to, ObmData data) throws CommunicationException {
		chip.send(to, data.getBinary());
	}
	
	public LoraData receive() {
		LoraData data = (LoraData) chip.receive();
		if (data != null) {
			received(data.getAddress(), new ObmData(data.getData()));
		}
		return data;
	}
}
