package com.firstlinecode.sand.client.things.autuator;

import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;

public interface IExecutor<T> {
	void execute(Iq iq, T action) throws ExecutionException;
}
