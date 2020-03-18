package com.firstlinecode.sand.protocols.concentrator;

import com.firstlinecode.sand.protocols.core.CommunicationNet;

public class NodeAddress<T> {
	private CommunicationNet communicationNet;
	private T address;
	
	public NodeAddress() {}
	
	public NodeAddress(CommunicationNet communicationNet, T address) {
		this.communicationNet = communicationNet;
		this.address = address;
	}
	
	public void setCommunicationNet(CommunicationNet communicationNet) {
		this.communicationNet = communicationNet;
	} 
	
	public CommunicationNet getCommunicationNet() {
		return communicationNet;
	}
	
	public void setAddress(T address) {
		this.address = address;
	}
	
	public T getAddress() {
		return address;
	}
}
