package com.thefirstlineofcode.sand.client.things.simple.light;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.things.simple.light.Flash;
import com.thefirstlineofcode.sand.protocols.things.simple.light.TurnOff;
import com.thefirstlineofcode.sand.protocols.things.simple.light.TurnOn;

public class LightPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(Flash.PROTOCOL),
				new NamingConventionParserFactory<Flash>(Flash.class));
		chatSystem.registerTranslator(Flash.class,
				new NamingConventionTranslatorFactory<Flash>(Flash.class));
		
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(TurnOn.PROTOCOL),
				new NamingConventionParserFactory<TurnOn>(TurnOn.class));
		chatSystem.registerTranslator(TurnOn.class,
				new NamingConventionTranslatorFactory<TurnOn>(TurnOn.class));
		
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(TurnOff.PROTOCOL),
				new NamingConventionParserFactory<TurnOff>(TurnOff.class));
		chatSystem.registerTranslator(TurnOff.class,
				new NamingConventionTranslatorFactory<TurnOff>(TurnOff.class));
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterTranslator(TurnOff.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(TurnOff.PROTOCOL));
		
		chatSystem.unregisterTranslator(TurnOn.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(TurnOn.PROTOCOL));
		
		chatSystem.unregisterTranslator(Flash.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(Flash.PROTOCOL));
	}

}
