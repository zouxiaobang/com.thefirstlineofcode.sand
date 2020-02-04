package com.firstlinecode.sand.client.things;

public interface IConcentrator extends IDevice, IObservable {
	void addNode(IThing child);
	void removeNode(IThing child);
	IThing[] getNodes();
}
