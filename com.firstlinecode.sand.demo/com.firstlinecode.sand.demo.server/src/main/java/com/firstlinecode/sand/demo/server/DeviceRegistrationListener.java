package com.firstlinecode.sand.demo.server;

import com.firstlinecode.granite.framework.core.event.IEventContext;
import com.firstlinecode.granite.framework.core.event.IEventListener;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.server.ibdr.DeviceRegistrationEvent;

public class DeviceRegistrationListener implements IEventListener<DeviceRegistrationEvent> {

	@Override
	public void process(IEventContext context, DeviceRegistrationEvent event) {
		// TODO Auto-generated method stub
		AccessControlEntry entry = (AccessControlEntry)event.getCustomizedTaskResult();
		System.out.println(String.format("%s -> %s: %s", entry.getUser(), entry.getDeviceId(), entry.getRole()));
	}

}
