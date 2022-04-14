package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.UnexpectedRequest;
import com.thefirstlineofcode.sand.client.things.actuator.IExecutor;
import com.thefirstlineofcode.sand.emulators.things.ILight;
import com.thefirstlineofcode.sand.protocols.actuator.LanActionException;
import com.thefirstlineofcode.sand.protocols.devices.light.Flash;

public class FlashExecutor implements IExecutor<Flash> {
	private Light light;
	
	public FlashExecutor(Light light) {
		this.light = light;
	}

	@Override
	public void execute(Iq iq, Flash action) {
		if (light.getSwitchState() != ILight.SwitchState.CONTROL)
			throw new ProtocolException(new UnexpectedRequest("Device is not in remote control state."));
		
		int repeat = action.getRepeat();
		if (repeat == 0)
			repeat = 1;
		
		try {
			light.flash(((Flash)action).getRepeat());
			
			synchronized (light) {
				try {
					light.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (LanActionException le) {
			// TODO Auto-generated catch block
			le.printStackTrace();
		}
	}
	
	private String getGlobalErrorCode(String model, String errorCode) {
		return String.format("%s-E%s", model, errorCode);
	}
}
