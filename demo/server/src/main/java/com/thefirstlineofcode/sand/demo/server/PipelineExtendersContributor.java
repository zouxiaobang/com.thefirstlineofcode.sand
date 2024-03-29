package com.thefirstlineofcode.sand.demo.server;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevices;
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
				new AclProcessor());
		
		configurator.registerNamingConventionParser(new IqProtocolChain(AuthorizedDevices.PROTOCOL),
				AuthorizedDevices.class);
		configurator.registerNamingConventionTranslator(AuthorizedDevices.class);
		configurator.registerSingletonXepProcessor(new IqProtocolChain(AuthorizedDevices.PROTOCOL),
				new AuthorizedDevicesProcessor());
		
		configurator.registerEventListener(DeviceRegistrationEvent.class, new DeviceRegistrationListener());
		configurator.registerEventListener(NodeCreationEvent.class, new NodeCreationListener());
		
		configurator.registerPipelinePreprocessor(new AclPipelinePreprocessor());
	}
	
}
