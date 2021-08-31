package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.core.annotations.BeanDependency;
import com.firstlinecode.granite.framework.core.config.IServerConfiguration;
import com.firstlinecode.granite.framework.core.config.IServerConfigurationAware;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.firstlinecode.sand.server.devices.IDeviceManager;

public class ConfirmedListener implements IEventListener<ConfirmedEvent>, IServerConfigurationAware {
	@BeanDependency
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
		jid.setNode(event.getNodeCreated().getConcentrator());
		jid.setDomain(domainName);
		jid.setResource(IConcentrator.LAN_ID_CONCENTRATOR);
		
		return jid;
	}

	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domainName = serverConfiguration.getDomainName();
	}

}
