package com.firstlinecode.sand.protocols.lora;

import java.io.Serializable;
import java.util.Random;

import com.firstlinecode.sand.protocols.core.Address;
import com.firstlinecode.sand.protocols.core.BadAddressException;
import com.firstlinecode.sand.protocols.core.CommunicationNet;

public class LoraAddress extends Address implements Serializable {
	private static final long serialVersionUID = -2095123770025458417L;
	
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
			throw new IllegalArgumentException("Invalid lora addresses.");
		
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
	
	public static LoraAddress randomLoraAddress() {
		return LoraAddress.randomLoraAddress(DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND);
	}
	
	public static LoraAddress randomLoraAddress(int frequencyBand) {
		return new LoraAddress(new Random().nextInt(LoraAddress.MAX_TWO_BYTES_ADDRESS - 1), frequencyBand);
	}

	@Override
	public Address parse(String addressString) throws BadAddressException {
		if (!addressString.startsWith("la$")) {
			throw new BadAddressException("Invalid LORA address.");
		}
		
		int conlonIndex = addressString.indexOf(':');
		if (conlonIndex == -1)
			throw new BadAddressException("Invalid LORA address.");
		
		String addressPart = addressString.substring(3, conlonIndex);
		String frequencyPart = addressString.substring(conlonIndex + 1);
		
		try {
			return  new LoraAddress(Long.parseLong(addressPart), Integer.parseInt(frequencyPart));
		} catch (NumberFormatException e) {
			throw new BadAddressException("Invalid LORA address.", e);
		}
	}

	@Override
	protected String getAddressString() {
		return String.format("la$%d:%d", address, frequencyBand);
	}
	
	@Override
	public CommunicationNet getCommunicationNet() {
		return CommunicationNet.LORA;
	}
}
