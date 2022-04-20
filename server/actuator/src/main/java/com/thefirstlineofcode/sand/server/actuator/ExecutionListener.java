package com.thefirstlineofcode.sand.server.actuator;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Stanza;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IIqResultProcessor;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class ExecutionListener implements IEventListener<ExecutionEvent>, IIqResultProcessor, IServerConfigurationAware {
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	private String domain;
	
	private Map<String, IExecutionCallback> callbacks;
	
	public ExecutionListener() {
		callbacks = new HashMap<>();
	}
	
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
		if (!deviceManager.isActionSupported(model, event.getExecution().getAction().getClass())) {
			throw new IllegalArgumentException(String.format("Unsupported action type: '%s'.", event.getExecution().getAction().getClass().getName()));
		}
		
		String deviceName = deviceManager.getDeviceNameByDeviceId(event.getDevice().getDeviceId());
		
		Iq iq = new Iq(event.getExecution(), Iq.Type.SET);
		
		if (isConcentrator && event.getNodeLanId() != null) {			
			iq.setTo(new JabberId(deviceName, domain, event.getNodeLanId()));
		} else {
			iq.setTo(new JabberId(deviceName, domain, DeviceIdentity.DEFAULT_RESOURCE_NAME));
		}
		
		synchronized (this) {
			if (event.getExecutionCallback() != null)
				callbacks.put(iq.getId(), event.getExecutionCallback());			
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

	@Override
	public boolean processResult(IProcessingContext context, Iq result) {
		IExecutionCallback callback = getCallback(result);
		
		return callback == null ? false : callback.processResult(context, result);
	}

	private synchronized IExecutionCallback getCallback(Stanza stanza) {
			String id = stanza.getId();
			
			IExecutionCallback callback = callbacks.get(id);
			if (callback != null)
				callbacks.remove(id);
			
			return callback;
	}

	@Override
	public boolean processError(IProcessingContext context, StanzaError error) {
		IExecutionCallback callback = getCallback(error);
		
		return callback == null ? false : callback.processError(context, error);
	}

}
