package com.thefirstlineofcode.sand.protocols.lora.dac;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:lora-dac", localName="allocation")
public class Allocation {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:lora-dac", "allocation");

	private long gatewayAddress;
	private int gatewayChannel;
	private long allocatedAddress;
	private int allocatedFrequencyBand;
	
	public long getGatewayAddress() {
		return gatewayAddress;
	}

	public void setGatewayAddress(long gatewayMasterAddress) {
		this.gatewayAddress = gatewayMasterAddress;
	}

	public int getGatewayChannel() {
		return gatewayChannel;
	}

	public void setGatewayChannel(int gatewayMasterFrequencyBand) {
		this.gatewayChannel = gatewayMasterFrequencyBand;
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
