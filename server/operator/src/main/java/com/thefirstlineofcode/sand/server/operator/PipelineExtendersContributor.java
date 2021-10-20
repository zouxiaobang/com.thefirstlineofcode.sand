package com.thefirstlineofcode.sand.server.operator;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.sand.protocols.operator.AuthorizeDevice;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {
	private static final IqProtocolChain PROTOCOL_CHAIN = new IqProtocolChain(AuthorizeDevice.PROTOCOL);

	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		configurator.
			registerNamingConventionParser(PROTOCOL_CHAIN, AuthorizeDevice.class).
			registerSingletonXepProcessor(PROTOCOL_CHAIN, new DeviceAuthorizationProcessor()).
			registerNamingConventionTranslator(AuthorizeDevice.class);
	}
}
