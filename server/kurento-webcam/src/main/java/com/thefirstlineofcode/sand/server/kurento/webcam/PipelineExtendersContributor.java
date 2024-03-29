package com.thefirstlineofcode.sand.server.kurento.webcam;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolChain;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator {
	private static final ProtocolChain IQ_PROTOCOL_CHAIN_SIGNAL = new IqProtocolChain(Signal.PROTOCOL);
	
	private static final SignalProcessor signalProcessor = new SignalProcessor();
	
	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		configurator.registerNamingConventionParser(IQ_PROTOCOL_CHAIN_SIGNAL, Signal.class);
		configurator.registerNamingConventionTranslator(Signal.class);
		configurator.registerSingletonXepProcessor(IQ_PROTOCOL_CHAIN_SIGNAL, signalProcessor);
		configurator.registerSessionListener(signalProcessor);
		configurator.registerEventListener(KurentoIceCandidateFoundEvent.class, new KurentoIceCandidateFoundListener());
	}

}
