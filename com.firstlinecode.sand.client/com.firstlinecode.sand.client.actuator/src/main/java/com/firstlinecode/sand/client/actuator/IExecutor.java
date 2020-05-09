package com.firstlinecode.sand.client.actuator;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;

public interface IExecutor<T> {
	void execute(Iq iq, T action) throws ExecutionException;
}
