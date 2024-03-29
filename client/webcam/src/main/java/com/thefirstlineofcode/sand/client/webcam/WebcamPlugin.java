package com.thefirstlineofcode.sand.client.webcam;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public class WebcamPlugin implements IPlugin {
	private static final ProtocolChain IQ_SIGNAL = new IqProtocolChain(Signal.PROTOCOL);

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(IQ_SIGNAL, new NamingConventionParserFactory<Signal>(Signal.class));
		chatSystem.registerTranslator(Signal.class, new NamingConventionTranslatorFactory<Signal>(Signal.class));
		chatSystem.registerApi(IWebcam.class, Webcam.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IWebcam.class);
		chatSystem.unregisterTranslator(Signal.class);
		chatSystem.unregisterParser(IQ_SIGNAL);
	}

}
