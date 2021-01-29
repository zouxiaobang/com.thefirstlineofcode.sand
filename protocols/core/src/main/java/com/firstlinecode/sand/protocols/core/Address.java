package com.firstlinecode.sand.protocols.core;

public abstract class Address {
	public abstract Address parse(String addressString) throws BadAddressException;
	protected abstract String getAddressString();
	public abstract CommunicationNet getCommunicationNet();
	
	@Override
	public String toString() {
		return getAddressString();
	}
}
