package com.thefirstlineofcode.sand.demo.server.acl;

import org.pf4j.Extension;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.sand.server.ibdr.DeviceRegistrationEvent;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {

	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		configurator.registerEventListener(DeviceRegistrationEvent.class, new DeviceRegistrationListener());
	}
	
}
