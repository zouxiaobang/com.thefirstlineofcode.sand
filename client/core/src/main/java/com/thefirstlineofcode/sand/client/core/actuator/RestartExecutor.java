package com.thefirstlineofcode.sand.client.core.actuator;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.UndefinedCondition;
import com.thefirstlineofcode.sand.client.core.IThing;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Restart;

public class RestartExecutor implements IExecutor<Restart> {
private IThing thing;
	
	public RestartExecutor(IThing thing) {
		this.thing = thing;
	}

	@Override
	public Object execute(Iq iq, Restart restart) throws ProtocolException {
		try {
			thing.restart();
			return null;
		} catch (ExecutionException e) {
			throw new ProtocolException(new UndefinedCondition(StanzaError.Type.CANCEL,
					ThingsUtils.getExecutionErrorDescription(null, e.getErrorCode())));
		}
	}
}
