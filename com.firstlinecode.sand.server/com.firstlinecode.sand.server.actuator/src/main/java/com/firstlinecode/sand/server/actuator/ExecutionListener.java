package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.config.IApplicationConfiguration;
import com.firstlinecode.granite.framework.core.config.IApplicationConfigurationAware;
import com.firstlinecode.granite.framework.core.event.IEventContext;
import com.firstlinecode.granite.framework.core.event.IEventListener;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.concentrator.Node;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class ExecutionListener implements IEventListener<ExecutionEvent>, IApplicationConfigurationAware {
	private static final String RESOURCE_DEVICE = "device";
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;

	private String domain;
	
	@Override
	public void process(IEventContext context, ExecutionEvent event) {
		Device actuator = event.getDevice();
		
		boolean isConcentrator = concentratorFactory.isConcentrator(event.getDevice());
		if (isConcentrator && event.getNodeLanId() != null) {
			IConcentrator concentrator = concentratorFactory.getConcentrator(event.getDevice());
			if (!concentrator.containsLanId(event.getNodeLanId())) {
				throw new IllegalArgumentException(String.format("Concentrator '%s' doesn't contain a node which's LAN ID is '%s'.",
						event.getDevice().getDeviceId(), event.getNodeLanId()));
			}
			
			Node node = concentrator.getNode(event.getNodeLanId());
			actuator = deviceManager.getByDeviceId(node.getDeviceId());
		}
		
		String model = deviceManager.getModel(actuator.getDeviceId());
		if (!deviceManager.isActionSupported(model, event.getExecute().getAction().getClass())) {
			throw new IllegalArgumentException(String.format("Unsupported action type: '%s'.", event.getExecute().getAction().getClass().getName()));
		}
		
		String deviceName = deviceManager.getDeviceNameByDeviceId(event.getDevice().getDeviceId());
		
		Iq iq = new Iq(event.getExecute(), Iq.Type.SET);
		JabberId target = getTarget(event, deviceName, isConcentrator);
		iq.setTo(target);
		
		context.write(target, iq);
	}

	private JabberId getTarget(ExecutionEvent event, String deviceName, boolean isConcentrator) {
		if (!isConcentrator && event.getNodeLanId() != null) {
			throw new IllegalArgumentException("Device which's ID is %s isn't a concentrator.");
		}
		
		JabberId to = new JabberId();
		to.setNode(deviceName);
		to.setDomain(domain);
		
		if (!isConcentrator) {
			to.setResource(RESOURCE_DEVICE);
			return to;
		}
		
		if (event.getNodeLanId() != null)
			to.setResource(event.getNodeLanId());
		else
			to.setResource(IConcentrator.LAN_ID_CONCENTRATOR);
		
		return to;
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		domain = appConfiguration.getDomainName();
	}

}
