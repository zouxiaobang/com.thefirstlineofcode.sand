package com.thefirstlineofcode.sand.server.concentrator;

import com.thefirstlinelinecode.sand.protocols.concentrator.NodeCreated;
import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirer;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirerAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class NodeConfirmationListener implements IEventListener<NodeConfirmationEvent>,
		IServerConfigurationAware, IEventFirerAware {
	@BeanDependency
	private IDeviceManager deviceManager;
	
	private String domainName;

	private IEventFirer eventFirer;
	
	@Override
	public void process(IEventContext context, NodeConfirmationEvent event) {
		String concentratorDeviceName = deviceManager.getDeviceNameByDeviceId(event.getConcentratorDeviceName());
		if (concentratorDeviceName == null)
			throw new RuntimeException("Concentrator not existed?");
		
		Iq result = new Iq(Iq.Type.RESULT, event.getRequestId());		
		result.setTo(getConcentratorJid(event));
		result.setObject(new NodeCreated(concentratorDeviceName, event.getNodeDeviceId(), event.getLanId(), event.getModel()));
		
		context.write(result);
		
		eventFirer.fire(new NodeCreationEvent(event.getConcentratorDeviceName(), event.getNodeDeviceId(),
				event.getLanId(), event.getConfirmer(), event.getCreationTime()));
	}

	private JabberId getConcentratorJid(NodeConfirmationEvent event) {
		JabberId jid = new JabberId();
		jid.setNode(event.getConcentratorDeviceName());
		jid.setDomain(domainName);
		jid.setResource(IConcentrator.LAN_ID_CONCENTRATOR);
		
		return jid;
	}

	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domainName = serverConfiguration.getDomainName();
	}

	@Override
	public void setEventFirer(IEventFirer eventFirer) {
		this.eventFirer = eventFirer;
	}

}
