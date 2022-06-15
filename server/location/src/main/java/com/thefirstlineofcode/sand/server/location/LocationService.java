package com.thefirstlineofcode.sand.server.location;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.sand.protocols.location.DeviceLocation;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

@Component
@Transactional
public class LocationService implements ILocationService {
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	@Override
	public List<DeviceLocation> locateDevices(List<String> deviceIds) {
		List<DeviceLocation> deviceLocations = new ArrayList<>();
		
		for (String deviceId : deviceIds) {
			if (!deviceManager.deviceIdExists(deviceId)) {
				throw new ProtocolException(new BadRequest(String.format(
						"The device which's ID is '%s' not exists. ", deviceId)));
			}
			
			DeviceLocation deviceLocation = new DeviceLocation();
			deviceLocation.setDeviceId(deviceId);
			
			String deviceName = deviceManager.getDeviceNameByDeviceId(deviceId);
			if (deviceName != null) {
				deviceLocation.setLocation(deviceName);
			} else {
				String concentratorDeviceName = concentratorFactory.getConcentratorDeviceNameByNodeDeviceId(deviceId);
				if (concentratorDeviceName == null)
					throw new ProtocolException(new ItemNotFound(String.format("Can't locate device which's device ID is '%s'.", deviceId)));
				
				String concentratorDeviceId = deviceManager.getDeviceIdByDeviceName(concentratorDeviceName);
				deviceLocation.setLocation(String.format("%s/%s", concentratorDeviceId,
						concentratorFactory.getConcentrator(concentratorDeviceId).getNodeByDeviceId(deviceId).getLanId()));
			}
			
			deviceLocations.add(deviceLocation);
		}
		
		return deviceLocations;
	}

}
