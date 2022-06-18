package com.thefirstlineofcode.sand.client.core.actuator;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.UndefinedCondition;
import com.thefirstlineofcode.sand.client.core.IThing;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Stop;

public class StopExecutor implements IExecutor<Stop> {
	private IThing thing;
	
	public StopExecutor(IThing thing) {
		this.thing = thing;
	}

	@Override
	public void execute(Iq iq, Stop stop) throws ProtocolException {
		try {
			thing.stop();
		} catch (ExecutionException e) {
			throw new ProtocolException(new UndefinedCondition(StanzaError.Type.MODIFY,
					ThingsUtils.getExecutionErrorDescription(null, e.getErrorCode())));
		}
	}
}
