package com.thefirstlineofcode.sand.server.webcam;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAllowed;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.auth.IAccountManager;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class SignalProcessor implements IXepProcessor<Iq, Signal> {
	
	@BeanDependency
	private IAccountManager accountManager;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@Override
	public void process(IProcessingContext context, Iq iq, Signal xep) {
		JabberId sessionJid = context.getJid();
		boolean isUserSession = false;
		boolean isDeviceSession = false;
		if (accountManager.exists(sessionJid.getNode())) {
			isUserSession = true;
		} else if (deviceManager.deviceNameExists(sessionJid.getNode())) {
			isDeviceSession = true;
		} else {			
			throw new ProtocolException(new NotAllowed(String.format("Neither user nor device. What thing are you?")));
		}
		
		if (iq.getTo() == null)
			throw new ProtocolException(new BadRequest("Null target JID."));
		
		if (isUserSession) {
			if (!deviceManager.deviceNameExists(iq.getTo().getNode())) {
				throw new ProtocolException(new ItemNotFound(String.format("Device named '%s' doesn't exist.", iq.getTo().getNode())));
			}			
			
			if (iq.getTo().getResource() == null)
				iq.getTo().setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
		}
		
		if (isDeviceSession) {
			if (!accountManager.exists(iq.getTo().getNode())) {
				throw new ProtocolException(new ItemNotFound(String.format("User named '%s' doesn't exist.", iq.getTo().getNode())));
			}
		}
		
		if (iq.getType() == Iq.Type.GET) {
			throw new ProtocolException(new BadRequest("IQ get type signal not allowed."));
		}
		
		iq.setFrom(sessionJid);
		
		context.write(iq);
	}

}
