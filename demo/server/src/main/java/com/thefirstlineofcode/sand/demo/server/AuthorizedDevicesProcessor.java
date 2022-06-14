package com.thefirstlineofcode.sand.demo.server;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevice;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevices;
import com.thefirstlineofcode.sand.protocols.location.DeviceLocation;
import com.thefirstlineofcode.sand.server.location.ILocationService;

public class AuthorizedDevicesProcessor implements IXepProcessor<Iq, AuthorizedDevices> {
	@BeanDependency
	private IAclService aclService;
	
	@BeanDependency
	private ILocationService locationService;

	@Override
	public void process(IProcessingContext context, Iq iq, AuthorizedDevices xep) {
		AccessControlList acl = aclService.getUserAcl(context.getJid().getNode());
		if (acl.getEntries() == null || acl.getEntries().size() == 0) {
			context.write(Iq.createResult(iq, new AuthorizedDevices()));
		} else {
			List<String> deviceIds = getAclDeviceIds(acl);
			List<DeviceLocation> deviceLocations = locationService.locateDevices(deviceIds);
			
			List<AuthorizedDevice> devices = getDevices(acl, deviceLocations);
			
			context.write(Iq.createResult(iq, new AuthorizedDevices(devices)));
		}
	}
	
	private List<String> getAclDeviceIds(AccessControlList acl) {
		List<String> deviceIds = new ArrayList<>();
		
		for (AccessControlEntry ace : acl.getEntries()) {
			deviceIds.add(ace.getDeviceId());
		}
		
		return deviceIds;
	}

	private List<AuthorizedDevice> getDevices(AccessControlList acl, List<DeviceLocation> deviceLocations) {
		List<AuthorizedDevice> devices = new ArrayList<>();
		for (int i = 0; i < deviceLocations.size(); i++) {
			AuthorizedDevice device = new AuthorizedDevice();
			device.setDeviceId(deviceLocations.get(i).getDeviceId());
			device.setDeviceLocation(deviceLocations.get(i).getLocation());
			device.setRole(getRole(acl, device.getDeviceId()));
			
			devices.add(device);
		}
		
		return devices;
	}
	
	private Role getRole(AccessControlList acl, String deviceId) {
		for (AccessControlEntry ace : acl.getEntries()) {
			if (ace.getDeviceId().equals(deviceId))
				return ace.getRole();
		}
		
		return null;
	}
}
