package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.config.IApplicationConfiguration;
import com.firstlinecode.granite.framework.core.config.IApplicationConfigurationAware;
import com.firstlinecode.granite.framework.core.event.IEventContext;
import com.firstlinecode.granite.framework.core.event.IEventListener;
import com.firstlinecode.granite.framework.im.IResourcesService;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.concentrator.Node;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class ExecutionListener implements IEventListener<ExecutionEvent>, IApplicationConfigurationAware {
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("resource.service")
	private IResourcesService resourceService;
	
	private String domain;
	
	@Override
	public void process(IEventContext context, ExecutionEvent event) {
		Device actuator = event.getDevice();
		if (event.getNodeLanId() != null) {
			IConcentrator concentrator = concentratorFactory.getConcentrator(event.getDevice());
			if (!concentrator.containsLanId(event.getNodeLanId())) {
				throw new IllegalArgumentException(String.format("Concentrator '%s' doesn't contain a node which's LAN ID is '%s'.",
						event.getDevice().getDeviceId(), event.getNodeLanId()));
			}
			
			Node node = concentrator.getNode(event.getNodeLanId());
			actuator = deviceManager.getByDeviceId(node.getDeviceId());
		}
		
		String mode = deviceManager.getMode(actuator.getDeviceId());
		if (!deviceManager.isActionSupported(mode, event.getExecute().getAction().getClass())) {
			throw new IllegalArgumentException(String.format("Unsupported action type: '%s'.", event.getExecute().getAction().getClass().getName()));
		}
		
		String deviceName = deviceManager.getDeviceNameByDeviceId(event.getDevice().getDeviceId());
		
		Iq iq = new Iq(event.getExecute(), Iq.Type.SET);
		iq.setTo(getTo(event, deviceName));
		
		context.write(getTarget(event, deviceName), iq);
	}

	private JabberId getTo(ExecutionEvent event, String deviceName) {
		JabberId to = new JabberId();
		to.setNode(deviceName);
		to.setDomain(domain);
		
		if (event.getNodeLanId() != null) {
			to.setResource(event.getNodeLanId());
		}
		
		return to;
	}

	private JabberId getTarget(ExecutionEvent event, String deviceName) {
		JabberId target = new JabberId();
		target.setNode(deviceName);
		target.setDomain(domain);
		
		if (event.getNodeLanId() != null) {
			target.setResource(IConcentrator.LAN_ID_CONCENTRATOR);
		}
		
		return target;
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		domain = appConfiguration.getDomainName();
	}

}
