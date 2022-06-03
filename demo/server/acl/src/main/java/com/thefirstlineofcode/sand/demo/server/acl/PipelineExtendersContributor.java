package com.thefirstlineofcode.sand.demo.server.acl;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.server.concentrator.NodeCreationEvent;
import com.thefirstlineofcode.sand.server.ibdr.DeviceRegistrationEvent;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {

	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		configurator.registerNamingConventionParser(new IqProtocolChain(AccessControlList.PROTOCOL),
				AccessControlList.class);
		configurator.registerNamingConventionTranslator(AccessControlList.class);
		configurator.registerSingletonXepProcessor(new IqProtocolChain(AccessControlList.PROTOCOL),
				new AccessControlListProcessor());
		
		configurator.registerEventListener(DeviceRegistrationEvent.class, new DeviceRegistrationListener());
		configurator.registerEventListener(NodeCreationEvent.class, new NodeCreationListener());
	}
	
}
