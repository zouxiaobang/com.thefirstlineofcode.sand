package com.thefirstlineofcode.sand.client.things.simple.gateway;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.things.simple.gateway.ChangeMode;

public class GatewayPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(ChangeMode.PROTOCOL),
				new NamingConventionParserFactory<ChangeMode>(ChangeMode.class));
		chatSystem.registerTranslator(ChangeMode.class,
				new NamingConventionTranslatorFactory<ChangeMode>(ChangeMode.class));
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterTranslator(ChangeMode.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(ChangeMode.PROTOCOL));
	}

}
