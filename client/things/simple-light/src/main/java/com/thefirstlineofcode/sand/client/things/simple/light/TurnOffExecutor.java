package com.thefirstlineofcode.sand.client.things.simple.light;

import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.UndefinedCondition;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.UnexpectedRequest;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.things.simple.light.TurnOff;

/**
 * @author xb.zou
 */
public class TurnOffExecutor implements IExecutor<TurnOff> {
    private final ILight light;

    public TurnOffExecutor(ILight light) {
        this.light = light;
    }

    @Override
    public Object execute(Iq iq, TurnOff action) throws ProtocolException {
        if (light.getSwitchState() != ILight.SwitchState.CONTROL) {
            throw new ProtocolException(new UnexpectedRequest(ThingsUtils.getExecutionErrorDescription(
                    light.getDeviceModel(), ILight.ERROR_CODE_NOT_REMOTE_CONTROL_STATE)));
        }

        if (light.getLightState() == ILight.LightState.ON) {
            try {
                light.turnOff();
            } catch (ExecutionException e) {
                throw new ProtocolException(new UndefinedCondition(StanzaError.Type.CANCEL,
                        ThingsUtils.getExecutionErrorDescription(light.getDeviceModel(), e.getErrorCode())));

            }
        }

        return null;
    }
}
