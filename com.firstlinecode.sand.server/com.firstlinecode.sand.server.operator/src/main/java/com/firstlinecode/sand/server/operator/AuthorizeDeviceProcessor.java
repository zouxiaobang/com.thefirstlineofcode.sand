package com.firstlinecode.sand.server.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.basalt.protocol.core.stream.error.Conflict;
import com.firstlinecode.granite.framework.core.annotations.Component;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.config.IConfiguration;
import com.firstlinecode.granite.framework.core.config.IConfigurationAware;
import com.firstlinecode.granite.framework.processing.IProcessingContext;
import com.firstlinecode.granite.framework.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.operator.AuthorizeDevice;
import com.firstlinecode.sand.server.device.IDeviceManager;

@Component("authorize.device.processor")
public class AuthorizeDeviceProcessor implements IXepProcessor<Iq, AuthorizeDevice>, IConfigurationAware {
	private static final String DEVICE_AUTHORIZATION_VALIDITY_TIME = "authorize.device.validity.time";
	private static final int DEFAULT_DEVICE_AUTHORIZATION_VALIDITY_TIME = 1000 * 60 * 30;
	
	private Logger logger = LoggerFactory.getLogger(AuthorizeDeviceProcessor.class);
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	private int deviceAuthorizationValidityTime;
	
	@Override
	public void process(IProcessingContext context, Iq iq, AuthorizeDevice xep) {
		if (iq.getType() != Iq.Type.SET)
			throw new ProtocolException(new BadRequest("Attribute 'type' should be set to 'set'."));
		
		if (!deviceManager.isValid(xep.getDeviceId())) {
			throw new ProtocolException(new BadRequest("Attribute 'type' should be set to 'set'."));
		}
		
		if (deviceManager.deviceIdExists(xep.getDeviceId()))
			throw new ProtocolException(new Conflict());
		
		deviceManager.authorize(xep.getDeviceId(), context.getJid().getBareIdString(), deviceAuthorizationValidityTime);
		
		if (logger.isInfoEnabled())
			logger.info("Device '{}' has authorized by authorizer '{}'.", xep.getDeviceId(), context.getJid().getBareIdString());
		
		context.write(context.getJid(), new Iq(Iq.Type.RESULT, iq.getId()));
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		deviceAuthorizationValidityTime = configuration.getInteger(DEVICE_AUTHORIZATION_VALIDITY_TIME, DEFAULT_DEVICE_AUTHORIZATION_VALIDITY_TIME);
	}

}
