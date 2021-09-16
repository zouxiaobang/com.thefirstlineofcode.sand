package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.basalt.protocol.core.IqProtocolChain;
import com.firstlinecode.granite.framework.core.pipeline.stages.PipelineExtendersContributorAdapter;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.EventListenerFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventListenerFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessorFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.SingletonXepProcessorFactory;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;

public class PipelineExtendersContributor extends PipelineExtendersContributorAdapter {
	@Override
	public IEventListenerFactory<?>[] getEventListenerFactories() {
		return new IEventListenerFactory<?>[] {
			new EventListenerFactory<>(ConfirmedEvent.class, new ConfirmedListener())
		};
	}
	
	@Override
	public IXepProcessorFactory<?, ?>[] getXepProcessorFactories() {
		return new IXepProcessorFactory<?, ?>[] {
			new SingletonXepProcessorFactory<>(
				new IqProtocolChain(CreateNode.PROTOCOL),
				new CreateNodeProcessor())
		};
	}
}
