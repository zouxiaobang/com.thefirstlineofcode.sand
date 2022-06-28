package com.thefirstlineofcode.sand.client.core.actuator;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.UndefinedCondition;
import com.thefirstlineofcode.sand.client.core.IThing;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.actuator.actions.ShutdownSystem;

public class ShutdownSystemExecutor implements IExecutor<ShutdownSystem> {
	private IThing thing;
	
	public ShutdownSystemExecutor(IThing thing) {
		this.thing = thing;
	}

	@Override
	public Object execute(Iq iq, ShutdownSystem shutdownSystem) throws ProtocolException {
		try {
			thing.shutdownSystem(shutdownSystem.isRestart());
			return null;
		} catch (ExecutionException e) {
			throw new ProtocolException(new UndefinedCondition(StanzaError.Type.CANCEL,
					ThingsUtils.getExecutionErrorDescription(null, e.getErrorCode())));
		}
	}

}
