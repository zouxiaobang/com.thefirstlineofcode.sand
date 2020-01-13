package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.MalformedJidException;
import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Stanza;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.granite.framework.core.annotations.Component;
import com.firstlinecode.granite.framework.core.config.IApplicationConfiguration;
import com.firstlinecode.granite.framework.core.config.IApplicationConfigurationAware;
import com.firstlinecode.sand.protocols.ibdr.DeviceIdentity;

@Component("default.device.registrar")
public class Registrar implements IDeviceRegistrar, IApplicationConfigurationAware {
	private String domainName;
	
	@Override
	public DeviceIdentity register(String deviceId) {
		try {
			return new DeviceIdentity(JabberId.parse(deviceId + "@" + domainName), Stanza.generateId());
		} catch (MalformedJidException e) {
			throw new ProtocolException(new BadRequest("Invalid device ID: " + deviceId));
		}
	}
	
	@Override
	public void remove(String deviceId) {
		// TODO
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		domainName = appConfiguration.getDomainName();
	}
	
}
