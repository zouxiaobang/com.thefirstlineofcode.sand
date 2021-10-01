package com.thefirstlineofcode.sand.protocols.lora;

import java.util.Random;

public class DualLoraAddress {
	public static final int MAX_CHANNEL = 31;
	
	private long address;
	private int channel;
	
	public DualLoraAddress() {}
	
	public DualLoraAddress(long address, int channel) {
		if (address < 0 || address > LoraAddress.MAX_FOUR_BYTES_ADDRESS)
			throw new IllegalArgumentException("Invalid dual lora addresses.");
		
		if (channel < 0 || channel > 31)
			throw new IllegalArgumentException("Lora channel must be range of 0~31.");
		
		this.address = address;
		this.channel = channel;
	}
	
	public long getAddress() {
		return address;
	}
	
	public void setAddress(long address) {
		this.address = address;
	}
	
	public int getChannel() {
		return channel;
	}
	
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DualLoraAddress) {
			DualLoraAddress other = (DualLoraAddress)obj;
			
			return address == other.address && channel == other.channel;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash += 31 * hash + address;
		hash += 31 * hash + channel;
		
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("DualLoraChipAddress[%d, %d, %d]", address,
				getMasterChipFrequencyBand(), getSlaveChipFrequencyBand());
	}
	
	private int getMasterChipFrequencyBand() {
		return channel * 2;
	}
	
	private int getSlaveChipFrequencyBand() {
		return channel * 2 + 1;
	}
	
	public LoraAddress getMasterChipAddress() {
		return new LoraAddress(address, getMasterChipFrequencyBand());
	}
	
	public LoraAddress getSlaveChipAddress() {
		return new LoraAddress(address, getSlaveChipFrequencyBand());
	}
	
	public static DualLoraAddress randomDualLoraAddress(int channel) {
		return new DualLoraAddress(new Random().nextInt(LoraAddress.MAX_TWO_BYTES_ADDRESS), channel);
	}
}
