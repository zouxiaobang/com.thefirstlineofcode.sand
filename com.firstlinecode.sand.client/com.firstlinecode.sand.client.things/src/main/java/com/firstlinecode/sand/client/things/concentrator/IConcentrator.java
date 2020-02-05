package com.firstlinecode.sand.client.things.concentrator;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.client.things.IObservable;
import com.firstlinecode.sand.client.things.IThing;
import com.firstlinecode.sand.client.things.actuator.IActuator;

public interface IConcentrator<T> extends IDevice, IObservable, IActuator {
	String createNode(Node<T> node) throws NodeCreationException;
	String createNode(IThing thing, T address) throws NodeCreationException;
	Node<T> removeNode(String nodeLanId);
	String[] getChildren();
	Node<T>[] getNodes();
	Node<T> getNode(String nodeLanId) throws NodeNotFoundException;
}
