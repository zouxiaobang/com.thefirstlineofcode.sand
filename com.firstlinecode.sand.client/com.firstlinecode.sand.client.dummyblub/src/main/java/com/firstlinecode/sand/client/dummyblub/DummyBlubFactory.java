package com.firstlinecode.sand.client.dummyblub;

import com.firstlinecode.sand.client.dummything.IDummyThingFactory;

public class DummyBlubFactory implements IDummyThingFactory<DummyBlub> {

	@Override
	public String getThingName() {
		return "Blub";
	}

	@Override
	public String getThingsName() {
		return "Blubs";
	}

	@Override
	public DummyBlub create() {
		return new DummyBlub();
	}

}
