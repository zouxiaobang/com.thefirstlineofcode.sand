package com.firstlinecode.sand.client.things.commuication;

public class Message<A, D> {
	private A address;
	private D data;
	
	public Message(A address, D data) {
		this.address = address;
		this.data = data;
	}
	
	public A getAddress() {
		return address;
	}
	
	public D getData() {
		return data;
	}
}
