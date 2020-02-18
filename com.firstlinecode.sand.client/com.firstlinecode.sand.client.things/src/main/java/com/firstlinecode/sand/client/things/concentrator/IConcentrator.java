package com.firstlinecode.sand.client.things.concentrator;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.client.things.IObservable;
import com.firstlinecode.sand.client.things.actuator.IActuator;

public interface IConcentrator<A> extends IDevice, IObservable, IActuator {
	String createNode(Node<A> node) throws NodeCreationException;
	Node<A> removeNode(String lanId);
	String[] getLanIds();
	Node<A> getNode(String lanId) throws NodeNotFoundException;
}
