package com.firstlinecode.sand.client.things.concentrator;

public interface IAddressConfiguationListener<K, V extends Enum<?>> {
	void configured(String deviceId, K address);
	void occurred(V error);
}
