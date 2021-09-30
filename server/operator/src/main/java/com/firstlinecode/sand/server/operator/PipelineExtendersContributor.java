package com.firstlinecode.sand.server.operator;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.core.pipeline.stages.PipelineExtendersContributorAdapter;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessorFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.SingletonXepProcessorFactory;
import com.firstlinecode.sand.protocols.operator.AuthorizeDevice;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersContributorAdapter {
	@Override
	protected NamingConventionParsableProtocolObject[] getNamingConventionParsableProtocolObjects() {
		return new NamingConventionParsableProtocolObject[] {
				new NamingConventionParsableProtocolObject(
						new IqProtocolChain(AuthorizeDevice.PROTOCOL),
						AuthorizeDevice.class
				)
		};
	}
	
	@Override
	protected Class<?>[] getNamingConventionTranslatableProtocolObjects() {
		return new Class<?>[] {
			AuthorizeDevice.class
		};
	}
	
	@Override
	public IXepProcessorFactory<?, ?>[] getXepProcessorFactories() {
		return new IXepProcessorFactory<?, ?>[] {
			new SingletonXepProcessorFactory<>(
					new IqProtocolChain(AuthorizeDevice.PROTOCOL),
					new DeviceAuthorizationProcessor()
			)
		};
	}
}
