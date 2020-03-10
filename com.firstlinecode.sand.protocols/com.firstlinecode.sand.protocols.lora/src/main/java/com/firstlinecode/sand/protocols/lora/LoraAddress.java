package com.firstlinecode.sand.protocols.lora;

import java.util.Random;

public class LoraAddress {
	public static final int DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_ADDRESS = 65535;
	public static final int DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_MASTER_CHIP_FREQUENCE_BAND = 62;
	public static final int DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_SLAVE_CHIP_FREQUENCE_BAND = 63;
	
	public static final LoraAddress DEFAULT_DYNAMIC_ADDRESS_CONFIGURATOR_NEGOTIATION_LORAADDRESS = new LoraAddress(
			DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_ADDRESS,
			DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_SLAVE_CHIP_FREQUENCE_BAND);
	
	public static final int DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND = 0;
	public static final int MAX_TWO_BYTES_ADDRESS = 65535;
	public static final long MAX_FOUR_BYTES_ADDRESS = 4294836225L;
	
	private long address;
	private int frequencyBand;
	
	public LoraAddress() {}
	
	public LoraAddress(long address, int frequencyBand) {
		if (address < 0 || address > MAX_FOUR_BYTES_ADDRESS)
			throw new IllegalArgumentException("Invalid dual lora addresses.");
		
		if (frequencyBand < 0 || frequencyBand > 63)
			throw new IllegalArgumentException("Lora frequency band must be range of 0~63.");
		
		this.address = address;
		this.frequencyBand = frequencyBand;
	}
	
	public long getAddress() {
		return address;
	}
	
	public void setAddress(long address) {
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
			
			return Long.compare(address, other.address) == 0 && frequencyBand == other.frequencyBand;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash += 31 * hash + address;
		hash += 31 * hash + frequencyBand;
		
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("LoraAddress[%d, %d]", address, frequencyBand);
	}
	
	public static LoraAddress randomLoraAddress() {
		return LoraAddress.randomLoraAddress(DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND);
	}
	
	public static LoraAddress randomLoraAddress(int frequencyBand) {
		return new LoraAddress(new Random().nextInt(LoraAddress.MAX_TWO_BYTES_ADDRESS - 1), frequencyBand);
	}
}
