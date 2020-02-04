package com.firstlinecode.sand.emulators.lora;

public interface ILoraNetworkListener {
	void sent(ILoraChip from, LoraAddress to, byte[] message);
	void received(ILoraChip from, ILoraChip to, byte[] message);
	void crashed(ILoraChip from, ILoraChip to, byte[] message);
	void lost(ILoraChip from, LoraAddress to, byte[] message);
	void addressChanged(ILoraChip chip, LoraAddress oldAddress);
}
