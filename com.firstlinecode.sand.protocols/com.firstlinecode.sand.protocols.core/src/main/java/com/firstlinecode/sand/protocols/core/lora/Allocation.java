package com.firstlinecode.sand.protocols.core.lora;

import com.firstlinecode.basalt.protocol.oxm.convention.annotations.ProtocolObject;

@ProtocolObject(namespace="http://firstlinecode.com/protocol/address-configuration", localName="allocation")
public class Allocation {
	private long gatewayAddress;
	private int gatewayFrequencyBand;
	private long allocatedAddress;
	private int allocatedFrequencyBand;
	
	public long getGatewayAddress() {
		return gatewayAddress;
	}
	
	public void setGatewayAddress(long gatewayAddress) {
		this.gatewayAddress = gatewayAddress;
	}
	
	public int getGatewayFrequencyBand() {
		return gatewayFrequencyBand;
	}
	
	public void setGatewayFrequencyBand(int gatewayFrequencyBand) {
		this.gatewayFrequencyBand = gatewayFrequencyBand;
	}
	
	public long getAllocatedAddress() {
		return allocatedAddress;
	}
	
	public void setAllocatedAddress(long allocatedAddress) {
		this.allocatedAddress = allocatedAddress;
	}
	
	public int getAllocatedFrequencyBand() {
		return allocatedFrequencyBand;
	}
	
	public void setAllocatedFrequencyBand(int allocatedFrequencyBand) {
		this.allocatedFrequencyBand = allocatedFrequencyBand;
	}
}
