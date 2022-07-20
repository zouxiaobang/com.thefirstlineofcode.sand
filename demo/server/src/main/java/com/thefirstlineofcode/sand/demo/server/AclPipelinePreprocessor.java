package com.thefirstlineofcode.sand.demo.server;

import com.thefirstlineofcode.basalt.xeps.ping.Ping;
import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAllowed;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAuthorized;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.auth.IAccountManager;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.parsing.IPipelinePreprocessor;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.location.LocateDevices;
import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class AclPipelinePreprocessor implements IPipelinePreprocessor {
	@BeanDependency
	private IAccountManager accountManager;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	@BeanDependency
	private IAclService aclService;
	
	@Override
	public String beforeParsing(JabberId from, String message) {
		return message;
	}

	@Override
	public Object afterParsing(JabberId from, Object object) {
		if (!(object instanceof Iq))
			return object;
		
		Iq iq = (Iq)object;
		if (iq.getObject() instanceof Execution) {
			return afterParsingExecution(from, iq);
		} else if (iq.getObject() instanceof LocateDevices) {
			return afterParsingLocateDevices(from, iq, (LocateDevices)iq.getObject());
		} else if (iq.getObject() instanceof Ping) {
			return afterParsingPing(from, iq);
		} else {
			return object;
		}
		

	}
	
	private Object afterParsingPing(JabberId from, Iq iq) {
		if (iq.getType() == Iq.Type.RESULT)
			return iq;
		
		return isOwnerOrController(from, iq.getTo()) ? iq : null;
	}
	
	private Object afterParsingExecution(JabberId from, Iq iq) {
		if (iq.getType() == Iq.Type.RESULT)
			return iq;
		
		return isOwnerOrController(from, iq.getTo()) ? iq : null;
	}

	private Object afterParsingLocateDevices(JabberId from, Iq iq, LocateDevices locateDevices) {
		if (iq.getType() == Iq.Type.RESULT)
			return iq;
		
		if (iq.getType() != Iq.Type.GET )
			throw new ProtocolException(new BadRequest("IQ type for LEPs location protocol Must be Iq.Type.GET."));
		
		if (locateDevices.getDeviceIds() == null || locateDevices.getDeviceIds().size() == 0)
			throw new ProtocolException(new BadRequest("Null device IDs or zero length device IDs."));
		
		for (String deviceId : locateDevices.getDeviceIds()) {			
			Role role = aclService.getRole(from.getNode(), deviceId);
			
			if (Role.OWNER != role && Role.CONTROLLER != role) {
				throw new ProtocolException(new NotAuthorized(String.format("You need authorization to locate the device. Device ID of the device is '%s'.", deviceId)));
			}
		}
		
		return iq;
	}
	
	private boolean isOwnerOrController(JabberId from, JabberId to) {
		if (!accountManager.exists(from.getNode())) {
			// Did a device send the message?
			throw new ProtocolException(new NotAllowed());
		}
		
		if (to == null)
			throw new ProtocolException(new BadRequest("Null target."));
		
		String deviceId = null;
		if (to.getResource() != null && !DeviceIdentity.DEFAULT_RESOURCE_NAME.equals(to.getResource())) {
			// The device is a LAN node.
			String concentratorDeviceName = to.getNode();
			String lanId = to.getResource();
			
			Device concentratorDevice = deviceManager.getByDeviceName(concentratorDeviceName);
			if (concentratorDevice == null) {
				throw new ProtocolException(new BadRequest(String.format("Concentrator which's device name is '%s' not exists.", concentratorDeviceName)));
			}
			
			if (!deviceManager.isConcentrator(concentratorDevice.getModel())) {
				throw new ProtocolException(new BadRequest(String.format("Device which's device name is '%s' isn't a concentrator.", concentratorDeviceName)));				
			}
			
			IConcentrator concentrator = concentratorFactory.getConcentrator(concentratorDevice.getDeviceId());
			Node node = concentrator.getNodeByLanId(lanId);
			if (node == null) {
				throw new ProtocolException(new BadRequest(String.format("There isn't a node which's LAN ID is '%s' under Concentrator which's device name is '%s'.", concentratorDeviceName)));
			}
			
			Device nodeDevice = deviceManager.getByDeviceId(node.getDeviceId());
			if (nodeDevice == null)
				throw new RuntimeException(String.format("Device which's device ID is %s not exists.", node.getDeviceId()));
			
			deviceId = nodeDevice.getDeviceId();
		} else {
			// The device is an edge thing.
			Device device = deviceManager.getByDeviceName(to.getNode());
			if (device == null)
				throw new ProtocolException(new BadRequest(String.format("Device which's device name is '%s' not exists.", to.getNode())));
				
			deviceId = device.getDeviceId();
		}
		
		Role role = aclService.getRole(from.getNode(), deviceId);
		return Role.OWNER == role || Role.CONTROLLER == role;
	}
}
