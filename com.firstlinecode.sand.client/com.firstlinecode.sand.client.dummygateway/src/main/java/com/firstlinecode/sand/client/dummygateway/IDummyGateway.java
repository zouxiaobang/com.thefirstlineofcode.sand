package com.firstlinecode.sand.client.dummygateway;

import com.firstlinecode.sand.client.dummything.IDummyThingFactory;

public interface IDummyGateway {
	void registerThingFactory(IDummyThingFactory<?> factory);
}
