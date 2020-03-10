package com.firstlinecode.sand.protocols.concentrator;

import com.firstlinecode.sand.protocols.core.ProtocolType;

public class NodeAddress<T> {
	private ProtocolType type;
	private T address;
	
	public NodeAddress() {}
	
	public NodeAddress(ProtocolType type, T address) {
		this.type = type;
		this.address = address;
	}
	
	public void setType(ProtocolType type) {
		this.type = type;
	} 
	
	public ProtocolType getType() {
		return type;
	}
	
	public void setAddress(T address) {
		this.address = address;
	}
	
	public T getAddress() {
		return address;
	}
}
