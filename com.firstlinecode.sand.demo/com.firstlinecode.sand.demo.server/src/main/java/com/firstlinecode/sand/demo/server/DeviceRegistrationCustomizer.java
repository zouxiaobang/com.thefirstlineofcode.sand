package com.firstlinecode.sand.demo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.device.IDeviceManager;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizer;

@Component
@Transactional
public class DeviceRegistrationCustomizer implements IDeviceRegistrationCustomizer {
	@Autowired
	private IDeviceManager deviceManager;
	
	@Autowired
	private IAccessControlListService aclService;

	@Override
	public Object executeCustomizedTask(String deviceId, DeviceIdentity identity) {
		// TODO Auto-generated method stub
		System.out.println("DeviceRegistrationCustomizer.executeCustomizedTask(...)");
		return null;
	}

	@Override
	public void processResult(IClientConnectionContext context, Object result) {
		// TODO Auto-generated method stub
		System.out.println("DeviceRegistrationCustomizer.processResult(...)");
	}

}
