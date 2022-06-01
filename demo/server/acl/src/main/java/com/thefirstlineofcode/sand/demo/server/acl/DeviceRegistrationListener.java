package com.thefirstlineofcode.sand.demo.server.acl;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.sand.server.ibdr.DeviceRegistrationEvent;

public class DeviceRegistrationListener extends AbstractDeviceActivationEventListener
			implements IEventListener<DeviceRegistrationEvent> {
	@Override
	public void process(IEventContext context, DeviceRegistrationEvent event) {		
		process(context, event.getDeviceId(), event.getAuthorizer());
	}
}
