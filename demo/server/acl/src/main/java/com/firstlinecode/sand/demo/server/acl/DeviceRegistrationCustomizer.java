package com.firstlinecode.sand.demo.server.acl;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.core.adf.data.IDataObjectFactory;
import com.firstlinecode.granite.framework.core.adf.data.IDataObjectFactoryAware;
import com.firstlinecode.granite.framework.core.annotations.BeanDependency;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.devices.Device;
import com.firstlinecode.sand.server.devices.DeviceAuthorization;
import com.firstlinecode.sand.server.devices.IDeviceManager;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizer;

@Extension
public class DeviceRegistrationCustomizer implements IDeviceRegistrationCustomizer, IDataObjectFactoryAware {
	@BeanDependency
	private IDeviceManager deviceManager;
	@BeanDependency
	private IAccessControlListService aclService;
	
	private IDataObjectFactory dataObjectFactory;

	@Override
	public Object executeCustomizedTask(String deviceId, DeviceIdentity identity) {
		Device device = deviceManager.getByDeviceId(deviceId);
		if (device == null) {
			throw new RuntimeException(String.format("No device which's Device ID is '%s' found.", deviceId));
		}
		
		DeviceAuthorization authorization = deviceManager.getAuthorization(deviceId);
		if (authorization == null)
			throw new RuntimeException(String.format("No device authorization which's authorized device's ID is '%s' found.", deviceId));
		
		if (authorization.getAuthorizer() == null)
			return null;
		
		AccessControlEntry ace = dataObjectFactory.create(AccessControlEntry.class);
		ace.setUser(authorization.getAuthorizer());
		ace.setDevice(deviceId);
		ace.setRole(Role.OWNER);
		aclService.add(ace);
		
		return ace;
	}

	@Override
	public void setDataObjectFactory(IDataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}
}
