package com.thefirstlineofcode.sand.client.thing;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Restart;
import com.thefirstlineofcode.sand.protocols.actuator.actions.ShutdownSystem;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Stop;

public class ThingPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(Stop.PROTOCOL),
				new NamingConventionParserFactory<Stop>(Stop.class));
		chatSystem.registerTranslator(Stop.class,
				new NamingConventionTranslatorFactory<Stop>(Stop.class));
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(Restart.PROTOCOL),
				new NamingConventionParserFactory<Restart>(Restart.class));
		chatSystem.registerTranslator(Restart.class,
				new NamingConventionTranslatorFactory<Restart>(Restart.class));
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(ShutdownSystem.PROTOCOL),
				new NamingConventionParserFactory<ShutdownSystem>(ShutdownSystem.class));
		chatSystem.registerTranslator(ShutdownSystem.class,
				new NamingConventionTranslatorFactory<ShutdownSystem>(ShutdownSystem.class));
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterTranslator(ShutdownSystem.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(ShutdownSystem.PROTOCOL));
		chatSystem.unregisterTranslator(Restart.class);		
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(Restart.PROTOCOL));
		chatSystem.unregisterTranslator(Stop.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(Stop.PROTOCOL));
	}

}
