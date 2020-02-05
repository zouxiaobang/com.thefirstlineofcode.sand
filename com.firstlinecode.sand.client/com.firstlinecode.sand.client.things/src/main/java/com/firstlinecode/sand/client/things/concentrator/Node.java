package com.firstlinecode.sand.client.things.concentrator;

import com.firstlinecode.sand.client.things.IThing;

public class Node<T> {
	private IThing thing;
	private T address;
	
	public Node(IThing thing, T address) {
		this.thing = thing;
		this.address = address;
	}
	
	public IThing getThing() {
		return thing;
	}
	
	public T getAddress() {
		return address;
	}
}
