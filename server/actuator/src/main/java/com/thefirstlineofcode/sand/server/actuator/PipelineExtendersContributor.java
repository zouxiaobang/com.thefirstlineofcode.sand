package com.thefirstlineofcode.sand.server.actuator;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.parsing.ProtocolParserFactory;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.routing.ProtocolTranslatorFactory;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecutionParserFactory;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecutionTranslatorFactory;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {
	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		configurator.
			registerParserFactory(
					new ProtocolParserFactory<>(new IqProtocolChain(Execute.PROTOCOL), new ExecutionParserFactory())).
			registerSingletonXepProcessor(
					new IqProtocolChain(Execute.PROTOCOL), new ExecutionProcessor()).
			registerTranslatorFactory(
					new ProtocolTranslatorFactory<>(Execute.class, new ExecutionTranslatorFactory())).
			registerEventListener(
					ExecutionEvent.class, new ExecutionListener());
	}
}
