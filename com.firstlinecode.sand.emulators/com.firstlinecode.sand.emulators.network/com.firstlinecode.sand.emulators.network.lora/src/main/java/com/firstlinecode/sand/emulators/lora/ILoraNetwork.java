package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;

public interface ILoraNetwork extends ICommunicationNetwork<LoraAddress, byte[]> {
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
	
	void setSingalQuality(ILoraChip chip1, ILoraChip chip2, LoraNetwork.SignalQuality signalQuality);
	void setSignalCrashedInterval(int interval);
	int getSignalCrashedInterval();
	void setSignalTransferTimeout(int timeout);
	int getSignalTransferTimeout();
}
