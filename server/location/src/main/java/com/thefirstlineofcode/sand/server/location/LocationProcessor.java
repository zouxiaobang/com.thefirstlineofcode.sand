package com.thefirstlineofcode.sand.server.location;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ServiceUnavailable;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.config.IConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.sand.protocols.location.DeviceLocation;
import com.thefirstlineofcode.sand.protocols.location.LocateDevices;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class LocationProcessor implements IXepProcessor<Iq, LocateDevices>, IConfigurationAware {
	private static final String CONFIG_KEY_ENABLED = "enabled";
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	private boolean enabled;
	
	@Override
	public void process(IProcessingContext context, Iq iq, LocateDevices xep) {
		if (enabled) {
			doProcess(context, iq, xep);
		} else {
			ServiceUnavailable error = StanzaError.create(iq, ServiceUnavailable.class);
			context.write(error);
		}
	}

	private void doProcess(IProcessingContext context, Iq iq, LocateDevices xep) {
		List<String> deviceIds = xep.getDeviceIds();
		
		if (deviceIds == null || deviceIds.size() == 0)
			throw new ProtocolException(new BadRequest("Null device IDs or zero length device IDs."));
		
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
				
				deviceLocation.setLocation(String.format("%s/%s", concentratorDeviceName, deviceId));
			}
			
			deviceLocations.add(deviceLocation);
		}

		xep = new LocateDevices();
		xep.setDeviceLocations(deviceLocations);
		Iq result = new Iq(Iq.Type.RESULT, xep, iq.getId());
		
		context.write(result);
	}
	
	@Override
	public void setConfiguration(IConfiguration configuration) {
		enabled = configuration.getBoolean(CONFIG_KEY_ENABLED, false);
	}
}
