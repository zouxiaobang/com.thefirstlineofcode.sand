package com.thefirstlineofcode.sand.client.things.simple.light;

import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.UndefinedCondition;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.UnexpectedRequest;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.things.simple.light.Flash;

public class FlashExecutor implements IExecutor<Flash> {
	private ILight light;
	
	public FlashExecutor(ILight light) {
		this.light = light;
	}

	@Override
	public Object execute(Iq iq, Flash action) {
		if (light.getSwitchState() != ILight.SwitchState.CONTROL)
			throw new ProtocolException(new UnexpectedRequest(ThingsUtils.getExecutionErrorDescription(
					light.getDeviceModel(), ILight.ERROR_CODE_NOT_REMOTE_CONTROL_STATE)));
		
		int repeat = action.getRepeat();
		if (repeat == 0)
			repeat = 1;
		
		try {			
			light.flash(repeat);
		} catch (ExecutionException e) {
			throw new ProtocolException(new UndefinedCondition(StanzaError.Type.CANCEL,
					ThingsUtils.getExecutionErrorDescription(light.getDeviceModel(), e.getErrorCode())));
		}
		
		return null;
	}
}
