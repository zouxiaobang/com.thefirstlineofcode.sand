package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.config.IApplicationConfiguration;
import com.firstlinecode.granite.framework.core.config.IApplicationConfigurationAware;
import com.firstlinecode.granite.framework.core.event.IEventContext;
import com.firstlinecode.granite.framework.core.event.IEventListener;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class ConfirmedListener implements IEventListener<ConfirmedEvent>, IApplicationConfigurationAware {
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	private String domainName;

	@Override
	public void process(IEventContext context, ConfirmedEvent event) {
		String deviceName = deviceManager.getDeviceNameByDeviceId(event.getNodeCreated().getConcentrator());
		if (deviceName == null)
			throw new RuntimeException("Concentrator not existed?");
		
		Iq result = new Iq(Iq.Type.RESULT, event.getRequestId());		
		result.setTo(getConcentratorJid(event));
		result.setObject(event.getNodeCreated());
		
		context.write(result);
	}

	private JabberId getConcentratorJid(ConfirmedEvent event) {
		JabberId jid = new JabberId();
		jid.setName(event.getNodeCreated().getConcentrator());
		jid.setDomain(domainName);
		jid.setResource(IConcentrator.LAN_ID_CONCENTRATOR);
		
		return jid;
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		domainName = appConfiguration.getDomainName();
	}

}
