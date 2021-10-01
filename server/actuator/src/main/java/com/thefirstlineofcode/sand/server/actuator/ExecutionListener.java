package com.thefirstlineofcode.sand.server.actuator;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

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
		
		if (isConcentrator) {			
			iq.setTo(new JabberId(deviceName, domain, event.getNodeLanId()));
		} else {
			iq.setTo(new JabberId(deviceName, domain, DeviceIdentity.DEFAULT_RESOURCE_NAME));
		}
		
		context.write(getTarget(event, deviceName, isConcentrator), iq);
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
		} else {			
			to.setResource(IConcentrator.LAN_ID_CONCENTRATOR);
		}
		
		return to;
	}

	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domain = serverConfiguration.getDomainName();
	}

}
