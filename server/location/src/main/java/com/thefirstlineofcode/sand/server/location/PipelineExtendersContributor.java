package com.thefirstlineofcode.sand.server.location;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.sand.protocols.location.LocateDevices;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {
	private static final ProtocolChain IQ_PROTOCOL_CHAIN_LOCATE_DEVICES = new IqProtocolChain(LocateDevices.PROTOCOL);
	
	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		configurator.registerNamingConventionParser(IQ_PROTOCOL_CHAIN_LOCATE_DEVICES, LocateDevices.class);
		configurator.registerNamingConventionTranslator(LocateDevices.class);
		configurator.registerSingletonXepProcessor(IQ_PROTOCOL_CHAIN_LOCATE_DEVICES, new LocationProcessor());
	}

}
