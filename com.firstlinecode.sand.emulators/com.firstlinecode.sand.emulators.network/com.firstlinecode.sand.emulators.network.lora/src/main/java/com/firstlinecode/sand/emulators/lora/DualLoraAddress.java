package com.firstlinecode.sand.emulators.lora;

import java.util.Random;

public class DualLoraAddress {
	private byte[] address;
	private int channel;
	
	public DualLoraAddress(byte[] address, int channel) {
		if (address == null)
			throw new IllegalArgumentException("Null address.");
		
		if (channel < 0 || channel > 31)
			throw new IllegalArgumentException("Lora channel must be range of 0~63.");
		
		this.address = address;
		this.channel = channel;
	}
	
	public byte[] getAddress() {
		return address;
	}
	
	public void setAddress(byte[] address) {
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
		if (obj instanceof LoraAddress) {
			DualLoraAddress other = (DualLoraAddress)obj;
			if (address.length != other.address.length)
				return false;
			
			for (int i = 0; i < address.length; i++) {
				if (address[i] != other.address[i])
					return false;
			}
			
			return channel == other.channel;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash += 31 * hash + address.hashCode();
		hash += 31 * hash + channel;
		
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("DualLoraChipAddress[%s, %d, %d]", getHexString(address),
				getMasterChipFrequencyBand(), getSlaveChipFrequencyBand());
	}
	
	public int getMasterChipFrequencyBand() {
		return channel * 2;
	}
	
	public int getSlaveChipFrequencyBand() {
		return channel * 2 + 1;
	}
	
	private String getHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("0x%02x", b));
		}
		
		return sb.toString();
	}
	
	public static DualLoraAddress randomDualLoraAddress(int channel) {
		byte[] address = new byte[2];
		new Random().nextBytes(address);
		
		return new DualLoraAddress(address, channel);
	}
}
