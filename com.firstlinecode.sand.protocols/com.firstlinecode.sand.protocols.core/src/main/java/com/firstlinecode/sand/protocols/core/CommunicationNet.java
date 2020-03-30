package com.firstlinecode.sand.protocols.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum CommunicationNet {
	LORA;
	
	private static ConcurrentMap<String, Address> instances = new ConcurrentHashMap<>();
	
	public Address parse(String addressString) throws BadAddressException {
		return getAddressInstance().parse(addressString);
	}
	
	private Address getAddressInstance() {
		Address address = instances.get(name());
		if (address != null)
			return address;
		
		String name = name();
		String className = String.format("%s.%s.%s%s%s",
				"com.sand.protocols",
				name,
				name.substring(0, 1).toUpperCase(),
				name.substring(1, name.length()),
				"Address"
		);
		
		try {
			address = (Address)Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Can't get address instance. Address name is %s and Address class name is %s.",
					name(), className), e);
		}
		
		return instances.putIfAbsent(name, address);
	}
}
