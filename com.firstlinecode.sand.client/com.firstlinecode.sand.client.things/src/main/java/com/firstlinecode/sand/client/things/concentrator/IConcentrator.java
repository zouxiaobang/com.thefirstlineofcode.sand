package com.firstlinecode.sand.client.things.concentrator;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.client.things.IObservable;
import com.firstlinecode.sand.client.things.IThing;

public interface IConcentrator extends IDevice, IObservable {
	void addNode(IThing node);
	void removeNode(IThing node);
	IThing[] getNodes();
}
