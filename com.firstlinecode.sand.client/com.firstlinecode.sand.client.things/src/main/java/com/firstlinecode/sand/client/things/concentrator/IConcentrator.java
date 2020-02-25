package com.firstlinecode.sand.client.things.concentrator;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.client.things.IObservable;
import com.firstlinecode.sand.client.things.actuator.IActuator;

public interface IConcentrator<A> extends IDevice, IObservable, IActuator {
	String createNode(Node<A> node) throws NodeCreationException;
	void setNodeEnabled(String lanId, boolean enabled) throws NodeNotFoundException;
	boolean isNodeEnabled(String lanId) throws NodeNotFoundException;
	Node<A> removeNode(String lanId) throws NodeNotFoundException;
	String[] getLanIds();
	Node<A> getNode(String lanId) throws NodeNotFoundException;
	String getLanId(A address);
	String getLanId(String deviceId);
}
