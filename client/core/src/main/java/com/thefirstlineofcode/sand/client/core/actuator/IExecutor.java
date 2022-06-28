package com.thefirstlineofcode.sand.client.core.actuator;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;

public interface IExecutor<T> {
	Object execute(Iq iq, T action) throws ProtocolException;
}
