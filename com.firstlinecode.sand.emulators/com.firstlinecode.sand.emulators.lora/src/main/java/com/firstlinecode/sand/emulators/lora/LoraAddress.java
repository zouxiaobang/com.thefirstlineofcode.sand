package com.firstlinecode.sand.emulators.lora;

public class LoraAddress {
	private byte[] address;
	private int frequencyBand;
	
	public LoraAddress(byte[] address, int frequencyBand) {
		if (address == null)
			throw new IllegalArgumentException("Null address.");
		
		if (frequencyBand < 1 || frequencyBand > 64)
			throw new IllegalArgumentException("Lora frequency band must be range of 1~64.");
		
		this.address = address;
		this.frequencyBand = frequencyBand;
	}
	
	public byte[] getAddress() {
		return address;
	}
	
	public void setAddress(byte[] address) {
		this.address = address;
	}
	
	public int getFrequencyBand() {
		return frequencyBand;
	}
	
	public void setFrequencyBand(int frequencyBand) {
		this.frequencyBand = frequencyBand;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LoraAddress) {
			LoraAddress other = (LoraAddress)obj;
			if (address.length != other.address.length)
				return false;
			
			for (int i = 0; i < address.length; i++) {
				if (address[i] != other.address[i])
					return false;
			}
			
			return frequencyBand == other.frequencyBand;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash += 31 * hash + address.hashCode();
		hash += 31 * hash + frequencyBand;
		
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("LoraNetwork[%s, %d]", getHexString(address), frequencyBand);
	}
	
	private String getHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("0x%02x", b));
		}
		
		return sb.toString();
	}
}
