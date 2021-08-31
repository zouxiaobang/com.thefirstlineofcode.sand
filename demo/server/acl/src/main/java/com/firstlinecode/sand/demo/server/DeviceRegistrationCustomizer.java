package com.firstlinecode.sand.demo.server;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.commons.osgi.OsgiUtils;
import com.firstlinecode.granite.framework.core.supports.data.IDataObjectFactory;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.DeviceAuthorization;
import com.firstlinecode.sand.server.device.IDeviceManager;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizer;

@Component
@Transactional
public class DeviceRegistrationCustomizer implements IDeviceRegistrationCustomizer, BundleContextAware {
	@Autowired
	private IDeviceManager deviceManager;
	@Autowired
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
	public void setBundleContext(BundleContext bundleContext) {
		dataObjectFactory = OsgiUtils.getService(bundleContext, IDataObjectFactory.class);
	}

}
