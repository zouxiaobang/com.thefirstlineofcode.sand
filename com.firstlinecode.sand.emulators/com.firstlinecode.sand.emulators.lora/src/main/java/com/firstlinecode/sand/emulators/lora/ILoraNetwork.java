package com.firstlinecode.sand.emulators.lora;

public interface ILoraNetwork {
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
	
	ILoraChip createChip(ILoraChip.Type type, byte[] address, int frequencyBand);
	ILoraChip createChip(ILoraChip.Type type, LoraAddress address);
	void setSingalQuality(ILoraChip chip1, ILoraChip chip2, LoraNetwork.SignalQuality signalQuality);
	void setSignalCrashedInterval(int interval);
	int getSignalCrashedInterval();
	void sendMessage(ILoraChip from, LoraAddress to, byte[] message);
	LoraMessage receiveMessage(ILoraChip target);
	void addListener(ILoraNetworkListener listener);
	boolean removeListener(ILoraNetworkListener listener);
}
