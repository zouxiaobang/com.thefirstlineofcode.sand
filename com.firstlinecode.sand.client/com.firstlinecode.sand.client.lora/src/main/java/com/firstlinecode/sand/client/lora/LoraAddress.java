package com.firstlinecode.sand.client.lora;

import java.util.Random;

import com.firstlinecode.sand.client.things.ThingsUtils;

public class LoraAddress {
	public static final byte[] DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_ADDRESS = new byte[] {0xF, 0xF, 0xF};
	public static final int DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_FREQUENCE_BAND = 63;
	public static final LoraAddress DEFAULLT_ADDRESS_CONFIGURATOR_LORA_ADDRESS = new LoraAddress(
			DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_ADDRESS,
			DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_FREQUENCE_BAND);
	
	public static final int DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND = 0;
	
	private byte[] address;
	private int frequencyBand;
	
	public LoraAddress(byte[] address, int frequencyBand) {
		if (address == null)
			throw new IllegalArgumentException("Null address.");
		
		if (frequencyBand < 0 || frequencyBand > 63)
			throw new IllegalArgumentException("Lora frequency band must be range of 0~63.");
		
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
		return String.format("LoraAddress[%s, %d]", ThingsUtils.getHexString(address), frequencyBand);
	}
	
	public static LoraAddress randomLoraAddress() {
		byte[] address = new byte[2];
		new Random().nextBytes(address);
		
		return new LoraAddress(address, DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND);
	}
	
	public static LoraAddress randomLoraAddress(int frequencyBand) {
		byte[] address = new byte[2];
		new Random().nextBytes(address);
		
		return new LoraAddress(address, frequencyBand);
	}
}
