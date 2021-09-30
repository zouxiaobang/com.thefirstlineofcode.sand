package com.firstlinecode.sand.server.actuator;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.core.pipeline.stages.PipelineExtendersContributorAdapter;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.EventListenerFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventListenerFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.parsing.IProtocolParserFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.parsing.ProtocolParserFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessorFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.SingletonXepProcessorFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.routing.IProtocolTranslatorFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.routing.ProtocolTranslatorFactory;
import com.firstlinecode.sand.protocols.actuator.Execute;
import com.firstlinecode.sand.protocols.actuator.oxm.ExecutionParserFactory;
import com.firstlinecode.sand.protocols.actuator.oxm.ExecutionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersContributorAdapter {
	
	@Override
	protected IProtocolParserFactory<?>[] getCustomizedParserFactories() {
		return new IProtocolParserFactory<?>[] {
			new ProtocolParserFactory<>(new IqProtocolChain(Execute.PROTOCOL), new ExecutionParserFactory())
		};
	}
	
	@Override
	protected IProtocolTranslatorFactory<?>[] getCustomizedTranslatorFactories() {
		return new ProtocolTranslatorFactory<?>[] {
			new ProtocolTranslatorFactory<>(Execute.class, new ExecutionTranslatorFactory())
		};
	}
	
	@Override
	public IEventListenerFactory<?>[] getEventListenerFactories() {
		return new IEventListenerFactory<?>[] {
			new EventListenerFactory<>(ExecutionEvent.class, new ExecutionListener())
		};
	}
	
	@Override
	public IXepProcessorFactory<?, ?>[] getXepProcessorFactories() {
		return new IXepProcessorFactory<?, ?>[] {
			new SingletonXepProcessorFactory<>(
				new IqProtocolChain(Execute.PROTOCOL),
				new ExecutionProcessor()
			)
		};
	}
}
