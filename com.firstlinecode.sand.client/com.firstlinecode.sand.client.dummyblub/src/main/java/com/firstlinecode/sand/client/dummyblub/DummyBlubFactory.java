package com.firstlinecode.sand.client.dummyblub;

import com.firstlinecode.sand.client.dummything.IDummyThingFactory;

public class DummyBlubFactory implements IDummyThingFactory<DummyBlub> {
	public static final String THING_NAME = "Blub";
	
	@Override
	public String getThingName() {
		return THING_NAME;
	}
	
	@Override
	public DummyBlub create() {
		return new DummyBlub();
	}

}
