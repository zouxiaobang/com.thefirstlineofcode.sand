package com.thefirstlineofcode.sand.demo.server.acl;

import org.pf4j.Extension;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirer;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirerAware;
import com.thefirstlineofcode.sand.server.devices.DeviceRegistered;
import com.thefirstlineofcode.sand.server.ibdr.DeviceRegistrationEvent;
import com.thefirstlineofcode.sand.server.ibdr.IDeviceRegistrationCustomizer;

@Extension
public class DeviceRegistrationCustomizer implements IDeviceRegistrationCustomizer, IEventFirerAware {
	private IEventFirer eventFirer;

	@Override
	public void executeCustomizedTask(DeviceRegistered registered) {
		eventFirer.fire(new DeviceRegistrationEvent(registered.deviceId, registered.deviceIdentity.getDeviceName(),
				registered.authorizer, registered.registrationTime));
	}

	@Override
	public void setEventFirer(IEventFirer eventFirer) {
		this.eventFirer = eventFirer;
	}
}
