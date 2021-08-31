package com.firstlinecode.sand.server.operator;

import org.pf4j.Extension;

import com.firstlinecode.basalt.protocol.core.ProtocolChain;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.core.pipeline.stages.PipelineExtendersContributorAdapter;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessorFactory;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.SingletonXepProcessorFactory;
import com.firstlinecode.sand.protocols.operator.AuthorizeDevice;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersContributorAdapter {
	@Override
	protected NamingConventionParsableProtocolObject[] getNamingConventionParsableProtocolObjects() {
		return new NamingConventionParsableProtocolObject[] {
				new NamingConventionParsableProtocolObject(
						ProtocolChain.first(Iq.PROTOCOL).next(AuthorizeDevice.PROTOCOL),
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
					ProtocolChain.first(Iq.PROTOCOL).next(AuthorizeDevice.PROTOCOL),
					new AuthorizeDeviceProcessor()
			)
		};
	}
}
