package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;

public interface ILoraNetwork extends ICommunicationNetwork<LoraAddress, byte[], LoraChipCreationParams> {
	public enum SignalQuality {
		GOOD(5),
		MEDUIM(15),
		BAD(30),
		BADDEST(70);
		
		private int packetLossRate;
		
		private SignalQuality(int packetLossRate) {
			this.packetLossRate = packetLossRate;
		}
		
		public int getPacketLossRate() {
			return packetLossRate;
		}
	}
	
	ILoraChip createChip(LoraAddress address);
	ILoraChip createChip(LoraAddress address, LoraChipCreationParams params);
	void setSingalQuality(LoraChip chip1, LoraChip chip2, LoraNetwork.SignalQuality signalQuality);
	void setSignalCrashedInterval(int interval);
	int getSignalCrashedInterval();
	void setSignalTransferTimeout(int timeout);
	int getSignalTransferTimeout();
}
