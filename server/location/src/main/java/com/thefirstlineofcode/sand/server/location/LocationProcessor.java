package com.thefirstlineofcode.sand.server.location;

import java.util.List;

import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.ServiceUnavailable;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.config.IConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.sand.protocols.location.DeviceLocation;
import com.thefirstlineofcode.sand.protocols.location.LocateDevices;

public class LocationProcessor implements IXepProcessor<Iq, LocateDevices>, IConfigurationAware {
	private static final String CONFIG_KEY_ENABLED = "enabled";
	
	@BeanDependency
	private ILocationService locationService;
	
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
		
		List<DeviceLocation> deviceLocations = locationService.locateDevices(deviceIds);
		
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
