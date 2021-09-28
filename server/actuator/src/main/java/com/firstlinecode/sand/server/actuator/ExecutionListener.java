package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.core.annotations.BeanDependency;
import com.firstlinecode.granite.framework.core.config.IServerConfiguration;
import com.firstlinecode.granite.framework.core.config.IServerConfigurationAware;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.concentrator.Node;
import com.firstlinecode.sand.server.devices.Device;
import com.firstlinecode.sand.server.devices.IDeviceManager;

public class ExecutionListener implements IEventListener<ExecutionEvent>, IServerConfigurationAware {
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	@BeanDependency
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
			to.setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
			return to;
		}
		
		if (event.getNodeLanId() != null)
			to.setResource(event.getNodeLanId());
		else
			to.setResource(IConcentrator.LAN_ID_CONCENTRATOR);
		
		return to;
	}

	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domain = serverConfiguration.getDomainName();
	}

}
