package com.firstlinecode.sand.server.operator;

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
import com.firstlinecode.sand.server.device.DeviceAuthorization;
import com.firstlinecode.sand.server.device.IDeviceManager;

@Component("device.authorization.processor")
public class DeviceAuthorizationProcessor implements IXepProcessor<Iq, DeviceAuthorization>, IConfigurationAware {
	private static final String DEVICE_AUTHORIZATION_VALIDITY_TIME = "device.authorization.validity.time";
	private static final int DEFAULT_DEVICE_AUTHORIZATION_VALIDITY_TIME = 1000 * 60 * 30;
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	private int deviceAuthorizationValidityTime;
	
	@Override
	public void process(IProcessingContext context, Iq iq, DeviceAuthorization xep) {
		if (iq.getType() != Iq.Type.SET)
			throw new ProtocolException(new BadRequest("Attribute 'type' should be set to 'set'."));
		
		if (deviceManager.deviceIdExists(xep.getDeviceId()))
			throw new ProtocolException(new Conflict());
		
		deviceManager.authorize(xep.getDeviceId(), context.getJid().getBareIdString(), deviceAuthorizationValidityTime);
		
		context.write(context.getJid(), new Iq(Iq.Type.RESULT, iq.getId()));
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		deviceAuthorizationValidityTime = configuration.getInteger(DEVICE_AUTHORIZATION_VALIDITY_TIME, DEFAULT_DEVICE_AUTHORIZATION_VALIDITY_TIME);
	}

}
