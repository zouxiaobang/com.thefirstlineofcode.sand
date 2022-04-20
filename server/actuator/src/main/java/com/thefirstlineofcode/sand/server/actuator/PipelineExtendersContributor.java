package com.thefirstlineofcode.sand.server.actuator;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.parsing.ProtocolParserFactory;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.routing.ProtocolTranslatorFactory;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecutionParserFactory;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecuteTranslatorFactory;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {
	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		ExecutionListener executionListener = new ExecutionListener();
		
		configurator.
			registerParserFactory(
					new ProtocolParserFactory<>(new IqProtocolChain(Execution.PROTOCOL), new ExecutionParserFactory())).
			registerSingletonXepProcessor(
					new IqProtocolChain(Execution.PROTOCOL), new ExecutionProcessor()).
			registerTranslatorFactory(
					new ProtocolTranslatorFactory<>(Execution.class, new ExecuteTranslatorFactory())).
			registerEventListener(
					ExecutionEvent.class, executionListener).
			registerIqResultProcessor(
					executionListener);
	}
}
