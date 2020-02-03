package com.firstlinecode.sand.client.things;

public interface IConcentrator extends IObservable, IThing {
	void addChild(IThing child);
	void removeChild(IThing child);
	IThing[] getChildren();
}
