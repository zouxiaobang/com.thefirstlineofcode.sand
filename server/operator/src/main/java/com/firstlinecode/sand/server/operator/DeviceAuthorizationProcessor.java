package com.firstlinecode.sand.server.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.operator.AuthorizeDevice;
import com.firstlinecode.sand.server.devices.DeviceAuthorizationDelegator;

public class DeviceAuthorizationProcessor implements IXepProcessor<Iq, AuthorizeDevice> {
	private Logger logger = LoggerFactory.getLogger(DeviceAuthorizationProcessor.class);
	
	@Dependency("device.authorization.delegator")
	private DeviceAuthorizationDelegator deviceAuthorizationDelegator;
	
	@Override
	public void process(IProcessingContext context, Iq iq, AuthorizeDevice xep) {
		if (iq.getType() != Iq.Type.SET)
			throw new ProtocolException(new BadRequest("Attribute 'type' should be set to 'set'."));
		
		deviceAuthorizationDelegator.authorize(xep.getDeviceId(), context.getJid().getNode());
		
		if (logger.isInfoEnabled())
			logger.info("Device '{}' has authorized by authorizer '{}'.", xep.getDeviceId(), context.getJid().getBareIdString());
		
		context.write(context.getJid(), new Iq(Iq.Type.RESULT, iq.getId()));
	}

}
