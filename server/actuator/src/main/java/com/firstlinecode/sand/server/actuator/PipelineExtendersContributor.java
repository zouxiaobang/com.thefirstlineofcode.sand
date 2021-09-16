package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.basalt.protocol.core.IqProtocolChain;
import com.firstlinecode.granite.framework.core.pipeline.stages.PipelineExtendersContributorAdapter;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.EventListenerFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventListenerFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessorFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.SingletonXepProcessorFactory;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class PipelineExtendersContributor extends PipelineExtendersContributorAdapter {
	@Override
	protected Class<?>[] getNamingConventionTranslatableProtocolObjects() {
		return new Class<?>[] {Execute.class};
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
