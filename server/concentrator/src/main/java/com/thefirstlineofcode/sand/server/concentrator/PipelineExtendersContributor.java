package com.thefirstlineofcode.sand.server.concentrator;

import org.pf4j.Extension;

import com.thefirstlinelinecode.sand.protocols.concentrator.CreateNode;
import com.thefirstlinelinecode.sand.protocols.concentrator.NodeCreated;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {
	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		configurator.registerNamingConventionParser(new IqProtocolChain(CreateNode.PROTOCOL), CreateNode.class);
		configurator.registerSingletonXepProcessor(new IqProtocolChain(CreateNode.PROTOCOL), new CreateNodeProcessor());
		configurator.registerNamingConventionTranslator(NodeCreated.class);
		configurator.registerEventListener(NodeConfirmationEvent.class, new NodeConfirmationListener());
	}
}
