package com.thefirstlinelinecode.sand.protocols.concentrator;

import com.thefirstlineofcode.sand.protocols.core.Address;
import com.thefirstlineofcode.sand.protocols.core.CommunicationNet;

public class NodeAddress<T extends Address> {
	private CommunicationNet communicationNet;	
	private String address;
	
	public NodeAddress() {}
	
	public NodeAddress(CommunicationNet communicationNet, String address) {
		this.communicationNet = communicationNet;
		this.address = address;
	}
	
	public void setCommunicationNet(CommunicationNet communicationNet) {
		this.communicationNet = communicationNet;
	}
	
	public CommunicationNet getCommunicationNet() {
		return communicationNet;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
}
