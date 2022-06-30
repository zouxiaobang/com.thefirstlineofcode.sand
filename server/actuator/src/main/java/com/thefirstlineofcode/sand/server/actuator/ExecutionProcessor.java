package com.thefirstlineofcode.sand.server.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.RecipientUnavailable;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.granite.framework.im.IResource;
import com.thefirstlineofcode.granite.framework.im.IResourcesService;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class ExecutionProcessor implements IXepProcessor<Iq, Execution> {
	private static final Logger logger = LoggerFactory.getLogger(ExecutionProcessor.class);
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	@BeanDependency
	private IResourcesService resourcesService;

	@Override
	public void process(IProcessingContext context, Iq iq, Execution xep) {
		JabberId target = iq.getTo();
		
		String deviceName = target.getNode();
		if (!deviceManager.deviceNameExists(deviceName)) {
			logger.warn("Device which's device name is '{}' not be found.", deviceName);
			throw new ProtocolException(new ItemNotFound(String.format("Device which's device name is '%s' not be found."), deviceName));
		}
		
		String deviceId = deviceManager.getDeviceIdByDeviceName(deviceName);
		String lanId = target.getResource();
		
		String model = null;
		if (doesExecutedOnNode(lanId)) {
			IConcentrator concentrator = concentratorFactory.getConcentrator(deviceId);
			if (concentrator == null) {
				logger.error("Concentrator which's device name is '{}' not exists.", deviceName);
				throw new RuntimeException(String.format("Concentrator which's device name is '%s' not exists.", deviceName));
			}
			
			Node node = concentrator.getNodeByLanId(lanId);
			if (node == null) {
				logger.error("Node which's LAN ID is '{}' not exists under concentrator which's device name is '{}'.", lanId, deviceName);
				throw new RuntimeException(String.format("Node which's LAN ID is '%s' not exists under concentrator which's device name is '%s'.", lanId, deviceName));				
			}
			
			model = deviceManager.getModel(node.getDeviceId());
		} else {
			model = deviceManager.getModel(deviceId);
		}
		
		if (!deviceManager.isActuator(model)) {
			logger.error("Can't do execution. Device which's model is '{}' isn't an actuator.", model);
			throw new RuntimeException(String.format("Can't do execution. Device which's model is '%s' isn't an actuator.", model));
		}
		
		if (!deviceManager.isActionSupported(model, xep.getAction().getClass())) {
			logger.error("Can't do execution. Action not be supported by device which's model is '{}'.", model);
			throw new RuntimeException(String.format("Can't do execution. Action not be supported by device which's model is '%s'.", model));
		}
		
		if (!doesExecutedOnNode(lanId) && (xep.isLanTraceable() || xep.getLanTimeout() != null)) {
			logger.warn("The device isn't a LAN node. Execution parameters 'lan-traceable' and 'lan-timeout'. will be ignored.", model);
		}
		
		JabberId edgeTarget = new JabberId(target.getNode(), target.getDomain(), DeviceIdentity.DEFAULT_RESOURCE_NAME);
		IResource resource = resourcesService.getResource(edgeTarget);
		if (resource == null) {
			logger.error("Can't deliver execution. Edge device which's device name is '{}' wasn't online.", deviceName);
			throw new ProtocolException(new RecipientUnavailable(String.format("Can't deliver execution. Edge device which's device name is '%s' isn't being online.", deviceName)));
		}
		
		context.write(edgeTarget, iq);
	}
	
	private boolean doesExecutedOnNode(String lanId) {
		return lanId != null && !DeviceIdentity.DEFAULT_RESOURCE_NAME.contains(lanId);
	}
}
